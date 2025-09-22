package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.RedisClient
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import java.time.Duration
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest

class ValkeyCacheClient(
    client: RedisClient,
    val keyMapper: ValkeyCacheKeyMapper,
    val mapper: ObjectMapper,
    val alleTreffTeller: BulkCacheSuksessTeller,
    val teller: BulkCacheTeller
)  {

    val log = getLogger(javaClass)


    val conn = client.connect().apply {
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Ex")
        }
    }

    inline fun <reified T> getOne(cache: CacheConfig, id: String) =
        conn.sync().get(keyMapper.toKey(cache,id))?.let { json ->
            mapper.readValue<T>(json)
        }

    fun putOne(cache: CacheConfig, id: String, value: Any, ttl: Duration)  {
        with(keyMapper.toKey(cache,id)) {
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
            keyMapper.fromKey(it)
        }.also {
            log.info("Fant ${it.size} nøkler i cache $cache")
        }

    @WithSpan
    inline fun <reified T> getMany(cache: CacheConfig, ids: Set<String>)  =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else conn.sync()
            .mget(*ids.map {
                    id -> keyMapper.toKey(cache,id)}.toTypedArray<String>()
            )
            .filter {
                it.hasValue()
            }
            .associate {
                keyMapper.fromKey(it.key) to mapper.readValue<T>(it.value)
            }.also {
                tellOgLog(cache.name, it.size, ids.size)
            }

    @WithSpan
    fun putMany(cache: CacheConfig, innslag: Map<String, Any>,  ttl: Duration) {
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

    private fun payloadFor(innslag: Map<String, Any>, cache: CacheConfig)=
        buildMap {
            innslag.forEach { (key, value) ->
                put(keyMapper.toKey(cache, key), mapper.writeValueAsString(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt id(er)")
    }
}
