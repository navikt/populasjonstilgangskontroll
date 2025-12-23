package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.micrometer.core.instrument.Tags.of
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.KClass


@Primary
@Component
class LettuceCacheClient(client: RedisClient, cfg: CacheConfig,
                         private val handler: CacheNøkkelHandler,
                         private val alleTreffTeller: BulkCacheSuksessTeller,
                         private val teller: BulkCacheTeller)  : CacheOperations {

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

    override fun delete( id: String,vararg caches: CachableConfig,) =
        caches.sumOf {
                cache -> conn.sync().del(handler.tilNøkkel(cache, id))
        }

    override fun <T : Any> getOne(id: String,clazz: KClass<T>,cache: CachableConfig, ) =
            conn.sync().get(handler.tilNøkkel(cache,id))?.let { json ->
                handler.fraJson(json, clazz)
        }

    override fun putOne(id: String, value: Any, ttl: Duration,cache: CachableConfig)  {
            conn.async().setex(handler.tilNøkkel(cache,id), ttl.seconds,handler.tilJson(value))
    }

    fun getAllKeys(cache: CachableConfig) =
            conn.sync().keys("${cache.name}::*")


     override fun <T : Any> getMany( ids: Set<String>,clazz: KClass<T>,cache: CachableConfig)  =
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
                    handler.idFraNøkkel(it.key) to handler.fraJson(it.value, clazz)
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

