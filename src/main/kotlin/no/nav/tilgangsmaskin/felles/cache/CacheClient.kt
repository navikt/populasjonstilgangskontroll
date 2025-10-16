package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.apache.commons.pool2.impl.GenericObjectPool
import org.slf4j.LoggerFactory.getLogger
import java.time.Duration
import kotlin.use

class CacheClient(
    val pool: GenericObjectPool<StatefulRedisConnection<String,String>>,
    val mapper: CacheNøkkelHandler,
    val alleTreffTeller: BulkCacheSuksessTeller,
    val teller: BulkCacheTeller
)  {

    val log = getLogger(javaClass)

    init {
        if (isLocalOrTest) {
            pool.borrowObject().use { conn ->
                conn.sync().configSet("notify-keyspace-events", "Exd")
            }
        }
    }

    @WithSpan
    inline fun <reified T> getOne(cache: CachableConfig, id: String) =
        pool.borrowObject().use { conn ->
            conn.sync().get(mapper.tilNøkkel(cache,id))?.let { json ->
                mapper.fraJson<T>(json)
            }
        }

    @WithSpan
    fun putOne(cache: CachableConfig, id: String, value: Any, ttl: Duration)  {
        pool.borrowObject().use { conn ->
            conn.async().setex(mapper.tilNøkkel(cache,id), ttl.seconds,mapper.tilJson(value))
        }
    }

    @WithSpan
    fun getAll(cache: String) =
        pool.borrowObject().use { conn ->
            conn.sync().keys("$cache::*").map {
                mapper.fraNøkkel(it)
            }.also {
                log.info("Fant ${it.size} nøkler i cache $cache")
            }
        }

    @WithSpan
    inline fun <reified T> getMany(cache: CachableConfig, ids: Set<String>)  =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else  pool.borrowObject().use { conn ->
            conn.sync()
                .mget(*ids.map {
                        id -> mapper.tilNøkkel(cache,id)}.toTypedArray<String>()
                )
                .filter {
                    it.hasValue()
                }
                .associate {
                    mapper.fraNøkkel(it.key) to mapper.fraJson<T>(it.value)
                }.also {
                    tellOgLog(cache.name, it.size, ids.size)
                }
        }

    @WithSpan
    fun putMany(cache: CachableConfig, innslag: Map<String, Any>, ttl: Duration) {
        if (innslag.isNotEmpty()) {
            log.trace("Bulk lagrer {} verdier for cache {} med prefix {}", innslag.size, cache.name, cache.extraPrefix)
            pool.borrowObject().use { conn ->
                conn.apply {
                    with(payloadFor(innslag, cache)) {
                        setAutoFlushCommands(false)
                        async().mset(this)
                        keys.forEach { key ->
                            async().expire(key, ttl.seconds)
                        }
                    }
                    flushCommands()
                    setAutoFlushCommands(true)
                }
            }
        }
    }

    private fun payloadFor(innslag: Map<String, Any>, cache: CachableConfig) =
        buildMap {
            innslag.forEach { (key, value) ->
                put(mapper.tilNøkkel(cache, key), mapper.tilJson(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }
}
