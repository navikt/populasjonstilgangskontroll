package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.reflect.KClass


@Primary
@Component
class LettuceCacheClient(lettuce: RedisClient, cfg: CacheConfig,
                         handler: CacheNøkkelHandler,
                         private val alleTreffTeller: BulkCacheSuksessTeller,
                         private val teller: BulkCacheTeller)  : AbstractCacheOperations(handler,cfg) {


    private val conn = lettuce.connect().apply {
        timeout = ofSeconds(30)
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    override fun ping() =
        conn.sync().ping()


    override fun delete(id: String, cache: CachableConfig) =
         conn.async().del(nøkkel(id, cache)).get() == 1L

    override fun <T : Any> get(id: String, clazz: KClass<T>, cache: CachableConfig ) =
            conn.async().get(nøkkel(id, cache)).get()?.let { json ->
                json(json, clazz)
        }

    override fun put(id: String, verdi: Any, ttl: Duration, cache: CachableConfig)  =
            conn.async().setex(nøkkel(id, cache), ttl.seconds,json(verdi)).get() == "OK"

     override fun <T : Any> get(ids: Set<String>, clazz: KClass<T>, cache: CachableConfig)  =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else  {
            conn.sync()
                .mget(*ids.map {
                        id -> nøkkel(id, cache)}.toTypedArray())
                .filter {
                    it.hasValue()
                }
                .associate {
                    id(it.key) to json(it.value, clazz)
                }.also {
                    tellOgLog(cache.name, it.size, ids.size)
                }
        }

    override
    fun put(verdier: Map<String, Any>, ttl: Duration, cache: CachableConfig) {
        if (verdier.isNotEmpty()) {
            log.trace("Bulk lagrer {} verdier for cache {} med prefix {}", verdier.size, cache.name, cache.extraPrefix)
                conn.apply {
                    with(payloadFor(verdier, cache)) {
                        setAutoFlushCommands(false)
                        async().mset(this)
                        keys.forEach {
                            async().expire(it, ttl.seconds)
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
                put(nøkkel(key, cache), json(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }
}

