package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.KeyValue
import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor.INITIAL
import io.lettuce.core.ScriptOutputType.MULTI
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.measureTimedValue

private const val SCRIPTS_COUNT_ALL_KEYS_LUA = "scripts/count-all-keys.lua"

@RetryingWhenRecoverableRestService
class ValkeyCacheOperations(client: RedisClient,
                            private val cfg: CacheConfig,
                            private val mapper: JsonMapper = VALKEY_MAPPER) : CacheOperations {

    private val log = getLogger(javaClass)
    private val countScript  = ClassPathResource(SCRIPTS_COUNT_ALL_KEYS_LUA).getContentAsString(UTF_8)

    private val conn = client.connect().apply {
        timeout = ofSeconds(cfg.timeout.seconds)
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    @WithSpan
    override fun delete(cache: CacheNøkkelConfig, id: String) =
        conn.sync().del(cache.tilNøkkel(id))

    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        runCatching {
            conn.sync().get(cache.tilNøkkel(id))?.let { json ->
                mapper.readValue(json, clazz.java)
            }
        }.getOrElse { ex ->
            log.warn("Cache getOne feilet for ${cache.name} nøkkel $id: ${ex.message}")
            null
        }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        conn.async().setex(cache.tilNøkkel(id), ttl.seconds, mapper.writeValueAsString(value))
    }

    @WithSpan
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T> =
        if (ids.isEmpty()) {
            emptyMap()
        } else {
            runCatching {
                val keys = ids.map { cache.tilNøkkel(it) }.toTypedArray()
                conn.sync()
                    .mget(*keys)
                    .filter { it.hasValue() }
                    .associate { it.toEntry(clazz) }
            }.getOrElse { ex ->
                log.warn("Cache getMany feilet for ${cache.name} med ${ids.size} nøkler: ${ex.message}")
                emptyMap()
            }
        }

    @WithSpan
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        if (innslag.isNotEmpty()) {
            log.trace("Bulk lagrer {} verdier for cache {} med prefix {}", innslag.size, cache.name, cache.extraPrefix)
            val payload = innslag.entries.associate {
                (key, value) -> cache.tilNøkkel(key) to mapper.writeValueAsString(value)
            }
            conn.apply {
                setAutoFlushCommands(false)
                try {
                    payload.forEach {
                        (key, value) -> async().setex(key, ttl.seconds, value)
                    }
                } finally {
                    flushCommands()
                    setAutoFlushCommands(true)
                }
            }
        }
    }

    override fun clear(cache: CacheNøkkelConfig) {
        check(!isProd) { "Clear er ikke støttet i prod for å unngå utilsiktet sletting av cache-innhold" }
        log.info("Tømmer cache {}", cache.name)
        val prefix = cache.tilNøkkel("")
        var cursor = INITIAL
        val args = ScanArgs().match("$prefix*").limit(10000)
        do {
            val result = conn.sync().scan(cursor, args)
            if (result.keys.isNotEmpty()) {
                conn.sync().del(*result.keys.toTypedArray())
            }
            cursor = result
        } while (!result.isFinished)
    }


    override fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long> {
        val prefixes = caches.map {
            "${it.tilNøkkel("")}*"
        }.toTypedArray()
        val (results, totalDuration) = eval(*prefixes)
        return caches.zip(results).associate {
            (cache, count) -> cache.fullName to count
        }.also {
            log.info("Cache sizes completed in single operation: {}, took {}ms", it, totalDuration.inWholeMilliseconds)
        }
    }

    private fun eval(vararg prefixes: String) =
        measureTimedValue {
            conn.sync().eval<List<Long>>(countScript, MULTI, emptyArray(), *prefixes)
        }

    private fun <T : Any> KeyValue<String, String>.toEntry(clazz: KClass<T>) =
        CacheNøkkel(key).id to mapper.readValue(value, clazz.java)
}
