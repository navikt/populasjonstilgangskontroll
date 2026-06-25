package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.CLEAR
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.DELETE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.GET_MANY
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.GET_ONE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.PUT_MANY
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.PUT_ONE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.FEILET
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.HIT
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.MISS
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.OK
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.measureTimedValue

@Component
class ValkeyCacheOperations(private val valkey: StringRedisTemplate, private val teller: ValkeyCacheTeller) :
    CacheOperations {

    private val log = getLogger(javaClass)

    init {
        if (isLocalOrTest) {
            valkey.execute { connection ->
                connection.serverCommands().setConfig("notify-keyspace-events", "Exd")
            }
        }
    }

    @WithSpan
    override fun delete(cache: CacheNøkkelConfig, id: String) =
        valkey.delete(cache.tilNøkkel(id))
            .also {
                teller.tell(DELETE, cache.name, OK)
            }

    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        runCatching {
            valkey.opsForValue().get(cache.tilNøkkel(id))?.let {
                VALKEY_MAPPER.readValue(it, clazz.java).also {
                    teller.tell(GET_ONE, cache.name, HIT)
                }
            } ?: run {
                teller.tell(GET_ONE, cache.name, MISS)
                null
            }
        }.getOrElse { e ->
            teller.tell(GET_ONE, cache.name, FEILET)
            log.info("Cache getOne feilet for {}, faller tilbake til tjenestekall", cache.name, e)
            null
        }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        runCatching {
            valkey.opsForValue().set(cache.tilNøkkel(id), VALKEY_MAPPER.writeValueAsString(value), ttl)
        }.onSuccess {
            teller.tell(PUT_ONE, cache.name, OK)
        }.onFailure {
            teller.tell(PUT_ONE, cache.name, FEILET)
            log.info("Cache putOne feilet for {} nøkkel {}: {}", cache.fullName, id, it.message, it)
        }
    }

    @WithSpan
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>) =
        when {
            ids.isEmpty() -> emptyMap()
            ids.size == 1 -> doGetOne(cache, ids, clazz)
            else -> doGetMany(cache, ids.toList(), clazz)
        }

    @WithSpan
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        when {
            innslag.isEmpty() -> return
            innslag.size == 1 -> doPutOne(cache, innslag, ttl)
            else -> doPutMany(cache, innslag, ttl.seconds)
        }
    }

    private fun <T : Any> doGetMany(cache: CacheNøkkelConfig,
                                    requestedIds: List<String>,
                                    clazz: KClass<T>): Map<String, T?> =
        measureTimedValue {
            runCatching {
                val keys = requestedIds.map(cache::tilNøkkel)
                val values = valkey.opsForValue().multiGet(keys).orEmpty()

                requestedIds.mapIndexedNotNull { index, id ->
                    values.getOrNull(index)?.let { value ->
                        id to VALKEY_MAPPER.readValue(value, clazz.java)
                    }
                }.toMap()
            }.getOrElse {
                teller.tell(GET_MANY, cache.name, FEILET, requestedIds.size)
                log.info("{} getMany feilet for {} med {} nøkler: {}",
                    javaClass.simpleName,
                    cache.fullName,
                    requestedIds.size,
                    it.message,
                    it)
                emptyMap()
            }
        }.let {
            teller.tell(GET_MANY, cache.name, HIT, it.value.size)
            teller.tell(GET_MANY, cache.name, MISS, requestedIds.size - it.value.size)
            log.info("getMany {} hentet {} av {} nøkler på {}ms",
                cache.fullName,
                it.value.size,
                requestedIds.size,
                it.duration.inWholeMilliseconds)
            it.value
        }

    private fun <T : Any> doGetOne(cache: CacheNøkkelConfig,
                                   ids: Set<String>,
                                   clazz: KClass<T>): Map<String, T> =
        ids.single().let { id ->
            getOne(cache, id, clazz)?.let {
                mapOf(id to it)
            }.orEmpty()
        }


    override fun clear(cache: CacheNøkkelConfig) {
        check(!isProd) { "Clear er ikke støttet i prod for å unngå utilsiktet sletting av cache-innhold" }
        log.info("Tømmer cache {}", cache.name)
        val prefix = cache.tilNøkkel("")
        var slettet = 0L
        val scanOptions = ScanOptions.scanOptions().match("$prefix*").count(10_000).build()

        valkey.executeWithStickyConnection { connection ->
            @Suppress("UNCHECKED_CAST")
            (connection.keyCommands().scan(scanOptions) as Cursor<ByteArray>).use { cursor ->
                val batch = mutableListOf<String>()

                cursor.forEach { keyBytes ->
                    batch += keyBytes.toString(UTF_8)
                    if (batch.size >= 10_000) {
                        slettet += valkey.delete(batch) ?: 0L
                        batch.clear()
                    }
                }

                if (batch.isNotEmpty()) {
                    slettet += valkey.delete(batch) ?: 0L
                }
            }
            null
        }
        teller.tell(CLEAR, cache.name, OK, slettet.toInt())
    }

    override fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long> {
        val prefixes = caches.map { "${it.tilNøkkel("")}*" }
        val (results, totalDuration) = measureTimedValue {
            @Suppress("UNCHECKED_CAST")
            valkey.execute(SCRIPT, emptyList(), *prefixes.toTypedArray()) as List<Long>
        }
        return caches.zip(results).associate { (cache, count) -> cache.fullName to count }
            .also { log.info("Cache størrelser {} slått opp, tok {}ms", it, totalDuration.inWholeMilliseconds) }
    }

    private fun doPutOne(cache: CacheNøkkelConfig,
                         innslag: Map<String, Any>,
                         ttl: Duration) {
        with(innslag.entries.single()) {
            putOne(cache, key, value, ttl)
        }
    }

    private fun doPutMany(cache: CacheNøkkelConfig,
                          innslag: Map<String, Any>,
                          ttl: Long) {
        val payload = innslag.entries.associate { (key, value) ->
            cache.tilNøkkel(key) to VALKEY_MAPPER.writeValueAsString(value)
        }
        val (resultat, varighet) = measureTimedValue {
            batch(payload, ttl)
        }
        resultat.onSuccess {
            teller.tell(PUT_MANY, cache.name, OK, innslag.size)
            log.info("Cache putMany {} lagret {} nøkler på {}ms",
                cache.fullName,
                innslag.size,
                varighet.inWholeMilliseconds)
        }
            .onFailure {
                teller.tell(PUT_MANY, cache.name, FEILET, innslag.size)
                log.info("Cache putMany feilet for {} med {} nøkler: {}", cache.fullName, innslag.size, it.message, it)
            }
    }

    private fun batch(payload: Map<String, String>, ttl: Long) =
        runCatching {
            valkey.executePipelined { connection ->
                payload.forEach { (key, value) ->
                    connection.stringCommands().setEx(key.toByteArray(), ttl, value.toByteArray())
                }
                null
            }
        }


    companion object {
        private val SCRIPT = RedisScript.of<List<*>>(ClassPathResource("scripts/count-all-keys.lua"), List::class.java)
    }
}
