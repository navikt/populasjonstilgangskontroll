package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisException
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.net.ConnectException
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import java.time.Duration
import no.nav.tilgangsmaskin.felles.RetryingWhenRecoverable
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import org.springframework.web.client.ResourceAccessException

//@RetryingWhenRecoverable([ConnectException::class, RedisException::class, ResourceAccessException::class])
class CacheClient(
    client: RedisClient,
    val nøkkelMapper: CacheNøkkelMapper,
    val mapper: ObjectMapper,
    val alleTreffTeller: BulkCacheSuksessTeller,
    val teller: BulkCacheTeller
)  {

    val log = getLogger(javaClass)


    val conn = client.connect().apply {
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    @WithSpan
    inline fun <reified T> getOne(cache: CachableConfig, id: String) =
        conn.sync().get(nøkkelMapper.tilNøkkel(cache,id))?.let { json ->
            mapper.readValue<T>(json)
        }

    @WithSpan
    fun putOne(cache: CachableConfig, id: String, value: Any, ttl: Duration)  {
        with(nøkkelMapper.tilNøkkel(cache,id)) {
            conn.apply {
                setAutoFlushCommands(false)
                async().set(this@with, mapper.writeValueAsString(value))
                async().expire(this@with, ttl.seconds)
                flushCommands()
                setAutoFlushCommands(true)
            }
        }
    }

    @WithSpan
    fun getAll(cache: String) =
        conn.sync().keys("$cache::*").map {
            nøkkelMapper.fraNøkkel(it)
        }.also {
            log.info("Fant ${it.size} nøkler i cache $cache")
        }

    @WithSpan
    inline fun <reified T> getMany(cache: CachableConfig, ids: Set<String>)  =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else conn.sync()
            .mget(*ids.map {
                    id -> nøkkelMapper.tilNøkkel(cache,id)}.toTypedArray<String>()
            )
            .filter {
                it.hasValue()
            }
            .associate {
                nøkkelMapper.fraNøkkel(it.key) to mapper.readValue<T>(it.value)
            }.also {
                tellOgLog(cache.name, it.size, ids.size)
            }

    @WithSpan
    fun putMany(cache: CachableConfig, innslag: Map<String, Any>, ttl: Duration) {
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
                put(nøkkelMapper.tilNøkkel(cache, key), mapper.writeValueAsString(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }
}
