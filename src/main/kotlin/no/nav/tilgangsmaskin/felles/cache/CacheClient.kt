package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisCommandTimeoutException
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.dao.QueryTimeoutException
import org.springframework.resilience.annotation.Retryable
import java.time.Duration

open class CacheClient(
    client: RedisClient,
    val handler: CacheNøkkelHandler,
    val alleTreffTeller: BulkCacheSuksessTeller,
    val teller: BulkCacheTeller)  {

    private val log = getLogger(javaClass)

    val conn = client.connect().apply {
        timeout = Duration.ofSeconds(30)
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    @RetryingWhenRecoverable([RedisCommandTimeoutException::class, QueryTimeoutException::class])
    @WithSpan
    fun delete(id: String,cache: CachableConfig) =
         conn.sync().del(handler.tilNøkkel(cache, id))


    @RetryingWhenRecoverable([RedisCommandTimeoutException::class, QueryTimeoutException::class])
    @WithSpan
    inline fun <reified T> getOne(id: String, cache: CachableConfig) =
            conn.sync().get(handler.tilNøkkel(cache,id))?.let { json ->
                handler.fraJson<T>(json)
        }

    @RetryingWhenRecoverable([RedisCommandTimeoutException::class, QueryTimeoutException::class])
    @WithSpan
    fun putOne(id: String, cache: CachableConfig, value: Any, ttl: Duration)  {
            conn.async().setex(handler.tilNøkkel(cache,id), ttl.seconds,handler.tilJson(value))
    }

    @WithSpan
    fun getAllKeys(cache: CachableConfig) =
            conn.sync().keys("${cache.name}::*")

    @RetryingWhenRecoverable([RedisCommandTimeoutException::class, QueryTimeoutException::class])
    @WithSpan
    inline fun <reified T> getMany(ids: Set<String>, cache: CachableConfig)  =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else  {
            conn.sync()
                .mget(*ids.map {
                        id -> handler.tilNøkkel(cache,id)}.toTypedArray<String>()
                )
                .filter {
                    it.hasValue()
                }
                .associate {
                    handler.idFraNøkkel(it.key) to handler.fraJson<T>(it.value)
                }.also {
                    tellOgLog(cache.name, it.size, ids.size)
                }
        }

    @RetryingWhenRecoverable([RedisCommandTimeoutException::class, QueryTimeoutException::class])
    @WithSpan
    fun putMany(innslag: Map<String, Any>, cache: CachableConfig, ttl: Duration) {
        if (innslag.isNotEmpty()) {
            log.trace("Bulk lagrer {} verdier for cache {} med prefix {}", innslag.size, cache.name, cache.extraPrefix)
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

    private fun payloadFor(innslag: Map<String, Any>, cache: CachableConfig) =
        buildMap {
            innslag.forEach { (key, value) ->
                put(handler.tilNøkkel(cache, key), handler.tilJson(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }

    fun tilNøkkel(cache: CachableConfig, id: String) = handler.tilNøkkel(cache, id)
}
