package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.KeyScanCursor
import io.lettuce.core.KeyValue
import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor.INITIAL
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.reflect.KClass
import kotlin.time.measureTime

@RetryingWhenRecoverableRestService
class ValkeyCacheClient(client: RedisClient, private val mapper: CacheNøkkelMapper,
                        private val alleTreffTeller: BulkCacheSuksessTeller,
                        private val teller: BulkCacheTeller,
                        private val cfg: CacheConfig) : CacheOperations {

    private val log = getLogger(javaClass)

    private val conn = client.connect().apply {
        timeout = ofSeconds(cfg.timeout.seconds)
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    @WithSpan
    override fun delete(cache: CacheNøkkelConfig, id: String) =
        conn.sync().del(mapper.tilNøkkel(cache, id))


    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        runCatching {
            conn.sync().get(mapper.tilNøkkel(cache, id))?.let { json ->
                mapper.fraJson(json, clazz)
            }
        }.getOrElse { ex ->
            log.warn("Cache getOne feilet for ${cache.name} nøkkel $id: ${ex.message}")
            null
        }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        conn.async().setex(mapper.tilNøkkel(cache, id), ttl.seconds, mapper.tilJson(value))
    }

    @WithSpan
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T> =
        if (ids.isEmpty()) {
            emptyMap()
        } else {
            runCatching {
                val keys = ids.map { mapper.tilNøkkel(cache, it) }.toTypedArray()
                conn.sync()
                    .mget(*keys)
                    .filter { it.hasValue() }
                    .associate {
                        it.fraJsonEntry(mapper, clazz)
                    }
                    .also { tellOgLog(cache.name, it.size, ids.size) }
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
                (key, value) -> mapper.tilJsonEntry(cache, key, value)
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

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }

    override fun tilNøkkel(cache: CacheNøkkelConfig, id: String) = mapper.tilNøkkel(cache, id)

    override fun clear(cache: CacheNøkkelConfig) {
        log.info("Tømmer cache {}", cache.name)
        val prefix = mapper.tilNøkkel(cache, "")
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

    override fun size(cache: CacheNøkkelConfig): Long {
        val prefix = mapper.tilNøkkel(cache, "")
        var count = 0L
        var cursor = INITIAL
        val args = ScanArgs().match("$prefix*").limit(10000)
        val totalDuration = measureTime {
            do {
                lateinit var result: KeyScanCursor<String>
                val duration = measureTime {
                    result = conn.sync().scan(cursor, args)
                    count += result.keys.size
                    cursor = result
                }
                log.info("Cache size scan iteration for {}: found {} keys in this batch, total so far {}, took {}ms", cache.name, result.keys.size, count, duration.inWholeMilliseconds)
            } while (!result.isFinished)
        }
        log.info("Cache size scan for {} completed: {} keys total, took {}ms", cache.name, count, totalDuration.inWholeMilliseconds)
        return count
    }

    private fun <T : Any> KeyValue<String, String>.fraJsonEntry(mapper: CacheNøkkelMapper, clazz: KClass<T>) =
        mapper.tilEntry(this, clazz)
}
