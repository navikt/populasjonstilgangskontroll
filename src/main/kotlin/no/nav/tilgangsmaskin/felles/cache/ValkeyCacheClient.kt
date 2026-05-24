package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
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
        conn.sync().get(mapper.tilNøkkel(cache, id))?.let { json ->
            mapper.fraJson(json, clazz)
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
            conn.sync()
                .mget(*ids.map { id -> mapper.tilNøkkel(cache, id) }.toTypedArray<String>())
                .filter { it.hasValue() }
                .associate { mapper.idFraNøkkel(it.key) to mapper.fraJson(it.value, clazz) }
                .also { tellOgLog(cache.name, it.size, ids.size) }
        }

    @WithSpan
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        if (innslag.isNotEmpty()) {
            log.trace("Bulk lagrer {} verdier for cache {} med prefix {}", innslag.size, cache.name, cache.extraPrefix)
            val payload = innslag.entries.associate { (key, value) -> mapper.tilEntry(cache, key, value) }
            conn.apply {
                setAutoFlushCommands(false)
                async().mset(payload)
                payload.keys.forEach { key -> async().expire(key, ttl.seconds) }
                flushCommands()
                setAutoFlushCommands(true)
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
        conn.sync().keys("$prefix*").forEach { conn.sync().del(it) }
    }
}
