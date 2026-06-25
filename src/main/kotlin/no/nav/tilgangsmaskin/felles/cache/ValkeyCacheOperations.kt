package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.CLEAR
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.DELETE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.GET_MANY
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.GET_ONE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.PUT_MANY
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.PUT_ONE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.DELVIS
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.FEILET
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.HIT
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.MISS
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.OK
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.TimeSource.Monotonic.markNow

@Component
class ValkeyCacheOperations(private val valkey: StringRedisTemplate,
                            private val teller: ValkeyCacheTeller,
                            private val mapper: JsonMapper = VALKEY_MAPPER) :
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
        markNow().let { start ->
            runCatching { valkey.delete(cache.tilNøkkel(id)) }
                .onSuccess {
                    teller.tellMedTid(DELETE, cache.name, OK, start.elapsedNow())
                }
                .onFailure {
                    teller.tellMedTid(DELETE, cache.name, FEILET, start.elapsedNow())
                    log.info("Cache delete feilet for {} nøkkel {}: {}", cache.fullName, id.maskFnr(), it.message, it)
                }
                .getOrElse { false }
        }

    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? {
        markNow().let { start ->
            return runCatching {
                valkey.opsForValue().get(cache.tilNøkkel(id))?.let { mapper.readValue(it, clazz.java) }
            }.onSuccess { verdi ->
                val resultat = if (verdi != null) HIT else MISS
                teller.tellMedTid(GET_ONE, cache.name, resultat, start.elapsedNow())
            }.onFailure {
                teller.tellMedTid(GET_ONE, cache.name, FEILET, start.elapsedNow())
                log.info("Cache getOne feilet for {}, faller tilbake til tjenestekall", cache.fullName, it)
            }.getOrNull()
        }
    }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        markNow().let { start ->
            runCatching {
                valkey.opsForValue().set(cache.tilNøkkel(id), mapper.writeValueAsString(value), ttl)
            }.onSuccess {
                teller.tellMedTid(PUT_ONE, cache.name, OK, start.elapsedNow())
            }.onFailure {
                teller.tellMedTid(PUT_ONE, cache.name, FEILET, start.elapsedNow())
                log.info("Cache putOne feilet for {} nøkkel {}: {}", cache.fullName, id, it.message, it)
            }
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
                                    clazz: KClass<T>): Map<String, T?> {
        markNow().let { start ->
            return runCatching {
                val values = valkey.opsForValue().multiGet(requestedIds.map(cache::tilNøkkel)).orEmpty()
                requestedIds.mapIndexedNotNull { index, id ->
                    values.getOrNull(index)?.let { value ->
                        id to mapper.readValue(value, clazz.java)
                    }
                }.toMap()
            }.onSuccess { verdier ->
                val varighet = start.elapsedNow()
                teller.tell(GET_MANY, cache.name, HIT, verdier.size)
                teller.tell(GET_MANY, cache.name, MISS, requestedIds.size - verdier.size)
                teller.tellMedTid(GET_MANY,
                    cache.name,
                    resultatForGetMany(verdier.size, requestedIds.size),
                    varighet,
                    0)
                log.info("getMany {} hentet {} av {} nøkler på {}ms",
                    cache.fullName, verdier.size, requestedIds.size, varighet.inWholeMilliseconds)
            }.onFailure {
                val varighet = start.elapsedNow()
                teller.tell(GET_MANY, cache.name, FEILET, requestedIds.size)
                teller.tellMedTid(GET_MANY, cache.name, FEILET, varighet, 0)
                log.info("{} getMany feilet for {} med {} nøkler: {}",
                    javaClass.simpleName, cache.fullName, requestedIds.size, it.message, it)
            }.getOrElse { emptyMap() }
        }
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
        val scanOptions = scanOptions().match("$prefix*").count(10_000).build()
        markNow().let { start ->
            runCatching {
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
            }.onSuccess {
                teller.tellMedTid(CLEAR, cache.name, OK, start.elapsedNow(), slettet.toInt())
            }.onFailure {
                teller.tellMedTid(CLEAR, cache.name, FEILET, start.elapsedNow())
            }.getOrThrow()
        }
    }

    override fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long> {
        markNow().let { start ->
            val prefixes = caches.map { "${it.tilNøkkel("")}*" }

            @Suppress("UNCHECKED_CAST")
            val results = valkey.execute(SCRIPT, emptyList(), *prefixes.toTypedArray()) as List<Long>
            val totalDuration = start.elapsedNow()
            return caches.zip(results).associate { (cache, count) -> cache.fullName to count }
                .also { log.info("Cache størrelser {} slått opp, tok {}ms", it, totalDuration.inWholeMilliseconds) }
        }
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
        markNow().let { start ->
            val payload = innslag.entries.associate { (key, value) ->
                cache.tilNøkkel(key) to mapper.writeValueAsString(value)
            }
            val resultat = pipeline(payload, ttl)
            val varighet = start.elapsedNow()
            resultat.onSuccess {
                teller.tellMedTid(PUT_MANY, cache.name, OK, varighet, innslag.size)
                log.info("Cache putMany {} lagret {} nøkler på {}ms",
                    cache.fullName,
                    innslag.size,
                    varighet.inWholeMilliseconds)
            }
                .onFailure {
                    teller.tell(PUT_MANY, cache.name, FEILET, innslag.size)
                    teller.tellTid(PUT_MANY, cache.name, FEILET, varighet)
                    log.info("Cache putMany feilet for {} med {} nøkler: {}",
                        cache.fullName,
                        innslag.size,
                        it.message,
                        it)
                }
        }
    }

    private fun pipeline(payload: Map<String, String>, ttl: Long) =
        runCatching {
            valkey.executePipelined { connection ->
                payload.forEach { (key, value) ->
                    connection.stringCommands().setEx(key.toByteArray(), ttl, value.toByteArray())
                }
                null
            }
        }

    private fun resultatForGetMany(hits: Int, requested: Int) =
        when {
            hits == requested -> HIT
            hits == 0 -> MISS
            else -> DELVIS
        }


    companion object {
        private val SCRIPT = RedisScript.of(ClassPathResource("scripts/count-all-keys.lua"), List::class.java)
    }
}
