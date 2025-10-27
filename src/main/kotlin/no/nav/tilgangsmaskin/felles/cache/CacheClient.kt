package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.ScriptOutputType.INTEGER
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import java.time.Duration

class CacheClient(
    client: RedisClient,
    val handler: CacheNøkkelHandler,
    val alleTreffTeller: BulkCacheSuksessTeller,
    val teller: BulkCacheTeller
)  {
    
    val conn = client.connect().apply {
        timeout = Duration.ofSeconds(30)
        if (isLocalOrTest) {
            sync().configSet("notify-keyspace-events", "Exd")
        }
    }

    val log = getLogger(javaClass)


    @WithSpan
    inline fun <reified T> getOne(cache: CachableConfig, id: String) =
            conn.sync().get(handler.tilNøkkel(cache,id))?.let { json ->
                handler.fraJson<T>(json)
        }

    @WithSpan
    fun putOne(cache: CachableConfig, id: String, value: Any, ttl: Duration)  {
            conn.async().setex(handler.tilNøkkel(cache,id), ttl.seconds,handler.tilJson(value))
    }

    @WithSpan
    fun getAll(cache: String) =
            conn.sync().keys("$cache::*").map {
                handler.idFraNøkkel(it)
            }.also {
                log.info("Fant ${it.size} nøkler i cache $cache")
            }


    @WithSpan
    inline fun <reified T> getMany(cache: CachableConfig, ids: Set<String>)  =
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
                put(handler.tilNøkkel(cache, key), handler.tilJson(value))
            }
        }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant $funnet verdier i cache $navn for $etterspurt identer")
    }

    fun count(prefix: String) : Int {
        val script = """local cursor = "0"
    local count = 0
    local prefix = ARGV[1]
    repeat
    local result = redis.call("SCAN", cursor, "MATCH", prefix .. "*", "COUNT", 1000)
    cursor = result[1]
    local keys = result[2]
    count = count + #keys
    until cursor == "0"
    return count
    """.trimIndent()
        return conn.sync().eval(script, INTEGER, emptyArray(), prefix)

    }
}
