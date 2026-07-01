package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
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
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.TimeSource.Monotonic.markNow

@Component
class ValkeyCacheOperations(private val valkey: StringRedisTemplate) : CacheOperations {

    private val log = getLogger(javaClass)

    init {
        if (isLocalOrTest) {
            runCatching {
                valkey.execute { connection ->
                    connection.serverCommands().setConfig("notify-keyspace-events", "Exd")
                }
            }.onFailure {
                log.warn("Klarte ikke å sette notify-keyspace-events=Exd for Valkey i lokal/test", it)
            }
        }
    }

    @WithSpan
    override fun delete(cache: CacheNøkkelConfig, id: String) =
        runCatching { valkey.unlink(cache.tilNøkkel(id)) }
            .onFailure {
                log.info("Cache delete feilet for {} nøkkel {}: {}", cache.fullName, id.maskFnr(), it.message, it)
            }.getOrElse { false }

    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? {
        return runCatching {
            valkey.opsForValue().get(cache.tilNøkkel(id))?.let { VALKEY_MAPPER.readValue(it, clazz.java) }
        }.onFailure {
            log.info("Cache getOne feilet for {}, faller tilbake til tjenestekall", cache.fullName, it)
        }.getOrNull()
    }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration?) {
        runCatching {
            val key = cache.tilNøkkel(id)
            val json = VALKEY_MAPPER.writeValueAsString(value)
            val ops = valkey.opsForValue()
            ttl?.let {
                ops.set(key, json, ttl)
            } ?: ops.set(key, json)
        }.onFailure {
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
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration?) {
        when {
            innslag.isEmpty() -> return
            innslag.size == 1 -> doPutOne(cache, innslag, ttl)
            else -> doPutMany(cache, innslag, ttl?.seconds)
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
                        id to VALKEY_MAPPER.readValue<T>(value, clazz.java)
                    }
                }.toMap()
            }.onSuccess { verdier ->
                val varighet = start.elapsedNow()
                log.info("getMany {} hentet {} av {} nøkler på {}ms",
                    cache.fullName, verdier.size, requestedIds.size, varighet.inWholeMilliseconds)
            }.onFailure {
                log.info("{} getMany feilet for {} med {} nøkler: {}",
                    javaClass.simpleName, cache.fullName, requestedIds.size, it.message, it)
            }.getOrElse { emptyMap() }
        }
    }

    private fun <T : Any> doGetOne(cache: CacheNøkkelConfig,
                                   ids: Set<String>,
                                   clazz: KClass<T>) =
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
        runCatching {
            valkey.executeWithStickyConnection { connection ->
                (connection.keyCommands().scan(scanOptions) as Cursor<ByteArray>).use { cursor ->
                    val batch = mutableListOf<String>()
                    cursor.forEach { keyBytes ->
                        batch += keyBytes.toString(UTF_8)
                        if (batch.size >= 10_000) {
                            slettet += valkey.unlink(batch) ?: 0L
                            batch.clear()
                        }
                    }

                    if (batch.isNotEmpty()) {
                        slettet += valkey.delete(batch) ?: 0L
                    }
                }
                null
            }
        }.getOrThrow()
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
                         ttl: Duration?) {
        with(innslag.entries.single()) {
            putOne(cache, key, value, ttl)
        }
    }

    private fun doPutMany(cache: CacheNøkkelConfig,
                          innslag: Map<String, Any>,
                          ttl: Long?) {
        markNow().let { start ->
            val payload = innslag.entries.associate { (key, value) ->
                cache.tilNøkkel(key) to VALKEY_MAPPER.writeValueAsString(value)
            }
            val resultat = pipeline(payload, ttl)
            val varighet = start.elapsedNow()
            resultat.onSuccess {
                log.info("Cache putMany {} lagret {} nøkler på {}ms",
                    cache.fullName,
                    innslag.size,
                    start.elapsedNow().inWholeMilliseconds)
            }.onFailure {
                    log.info("Cache putMany feilet for {} med {} nøkler: {}",
                        cache.fullName,
                        innslag.size,
                        it.message,
                        it)
                }
        }
    }

    private fun pipeline(payload: Map<String, String>, ttl: Long?) =
        runCatching {
            valkey.executePipelined { connection ->
                payload.forEach { (key, value) ->
                    if (ttl != null) {
                        connection.stringCommands().setEx(key.toByteArray(), ttl, value.toByteArray())
                    }
                    else {
                        payload.forEach { (key, value) ->
                            connection.stringCommands().set(key.toByteArray(), value.toByteArray())
                        }
                    }
                    null
                }
            }
        }
    companion object {
        private val SCRIPT = RedisScript.of(ClassPathResource("scripts/count-all-keys.lua"), List::class.java)
    }
}
