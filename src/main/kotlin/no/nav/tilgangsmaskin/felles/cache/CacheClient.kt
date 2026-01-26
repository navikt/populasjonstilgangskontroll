package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.cache.CacheAdapter.Companion.VALKEY
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.KClass

@Component
@Primary
class CacheClient(client: RedisClient, cfg: CacheConfig,
                         private val handler: CacheNøkkelHandler,
                         private val alleTreffTeller: BulkCacheSuksessTeller,
                         private val teller: BulkCacheTeller)  : CacheOperations, Pingable {

    private val log = getLogger(javaClass)

    val conn = client.connect().apply {
        timeout = Duration.ofSeconds(30)
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    override fun ping() =
        conn.sync().ping()

    override val pingEndpoint = RedisURI.Builder
        .redis(cfg.host, cfg.port)
        .withSsl(true)
        .build().toURI().toString()

    override val name = VALKEY

    override fun delete( id: String, cache: CachableConfig) =
                conn.sync().del(handler.nøkkel(id,cache))

    override fun <T : Any> getOne(id: String, clazz: KClass<T>, cache: CachableConfig, ) =
        conn.sync().get(handler.nøkkel(id,cache))?.let { json ->
            handler.json(json, clazz)
        }

    override fun putOne(id: String, value: Any, ttl: Duration,cache: CachableConfig)  {
        conn.async().setex(handler.nøkkel(id,cache), ttl.seconds,handler.json(value))
    }


    override fun <T : Any> getMany( ids: Set<String>,clazz: KClass<T>,cache: CachableConfig)  =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else  {
            conn.sync()
                .mget(*ids.map {
                        id -> handler.nøkkel(id,cache)}.toTypedArray<String>()
                )
                .filter {
                    it.hasValue()
                }
                .associate {
                    handler.id(it.key) to handler.json(it.value, clazz)
                }.also {
                    tellOgLog(cache.name, it.size, ids.size)
                }
        }

    override
    fun putMany(innslag: Map<String, Any>, ttl: Duration,cache: CachableConfig, ) {
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
                put(handler.nøkkel(key,cache), handler.json(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }

    override fun tilNøkkel(cache: CachableConfig, id: String) = handler.nøkkel(id,cache)
}
