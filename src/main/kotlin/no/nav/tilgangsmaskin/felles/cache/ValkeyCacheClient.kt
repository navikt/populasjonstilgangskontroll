package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.LettuceFutures.awaitAll
import io.lettuce.core.KeyValue
import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor.INITIAL
import io.lettuce.core.ScriptOutputType.MULTI
import io.micrometer.core.instrument.Tags.of
import io.opentelemetry.instrumentation.annotations.WithSpan
import jakarta.annotation.PreDestroy
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.collections.emptyMap
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.measureTimedValue

@RetryingWhenRecoverableRestService
class ValkeyCacheClient(client: RedisClient,
                        private val mapper: CacheNøkkelMapper,
                        private val alleTreffTeller: BulkCacheSuksessTeller,
                        private val teller: BulkCacheTeller,
                        private val cfg: CacheConfig) : CacheOperations {

    private val log = getLogger(javaClass)
    private val countScript  = script()
    private val conn = connect(client)
    private val batchConn = connect(client)

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
            log.info("Cache getOne feilet for {}, faller tilbake til tjenestekall", cache.name, ex)
            null
        }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        conn.async().setex(mapper.tilNøkkel(cache, id), ttl.seconds, mapper.tilJson(value))
    }

    @WithSpan
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>) =
        when {
            ids.isEmpty() -> emptyMap()
            ids.size == 1 -> getOne(cache, ids.first(), clazz)?.let { mapOf(ids.first() to it) } ?: emptyMap()
            else -> {
                val (result, elapsed) = measureTimedValue {
                    runCatching {
                        val keys = ids.map { mapper.tilNøkkel(cache, it) }.toTypedArray()
                        conn.sync()
                            .mget(*keys)
                            .filter { it.hasValue() }
                            .associate {
                                it.fraJsonEntry(mapper, clazz)
                            }
                    }.getOrElse { ex ->
                        log.info("Cache getMany feilet for ${cache.fullName} med ${ids.size} nøkler, faller tilbake til tjenestekall: ${ex.message}")
                        emptyMap()
                    }
                }
                result.also { tellOgLog(cache.fullName, it.size, ids.size, elapsed) }
            }
        }

    @WithSpan
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        when {
            innslag.isEmpty() -> return
            innslag.size == 1 -> putOne(cache, innslag.keys.single(), innslag.values.single(), ttl)
            else -> {
                val (_, elapsed) = measureTimedValue {
                    val payload = innslag.entries.associate {
                            (key, value) -> mapper.tilJsonEntry(cache, key, value)
                    }
                    runCatching {
                        synchronized(batchConn) {
                            batchConn.setAutoFlushCommands(false)
                            try {
                                val futures = payload.map {
                                        (key, value) -> batchConn.async().setex(key, ttl.seconds, value)
                                }
                                batchConn.flushCommands()
                                awaitAll(cfg.timeout, *futures.toTypedArray())
                            } finally {
                                batchConn.setAutoFlushCommands(true)
                            }
                        }
                    }.onFailure { ex ->
                        log.info("Cache putMany feilet for ${cache.fullName} med ${innslag.size} nøkler: ${ex.message}")
                    }
                }
                log.info("putMany {} lagret {} nøkler på {}ms", cache.fullName, innslag.size, elapsed.inWholeMilliseconds)
            }
        }
    }

    fun tellOgLog(navn: String, funnet: Int, etterspurt: Int, elapsed: kotlin.time.Duration) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.info("getMany {} hentet {} av {} nøkler på {}ms", navn, funnet, etterspurt, elapsed.inWholeMilliseconds)
    }

    override fun tilNøkkel(cache: CacheNøkkelConfig, id: String) = mapper.tilNøkkel(cache, id)

    override fun clear(cache: CacheNøkkelConfig) {
        check(!isProd) {
            "Clear er ikke støttet i prod for å unngå utilsiktet sletting av cache-innhold"
        }
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


    override fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long> {
        val prefixes = caches.map {
            "${mapper.tilNøkkel(it, "")}*"
        }.toTypedArray()
        val (results, totalDuration) = eval(*prefixes)
        return caches.zip(results).associate {
            (cache, count) -> cache.fullName to count
        }.also {
            log.info("Cache sizes completed in single operation: {}, took {}ms", it, totalDuration.inWholeMilliseconds)
        }
    }

    private fun eval(vararg prefixes: String) =
        measureTimedValue {
            conn.sync().eval<List<Long>>(countScript, MULTI, emptyArray(), *prefixes)
        }


    private fun <T : Any> KeyValue<String, String>.fraJsonEntry(mapper: CacheNøkkelMapper, clazz: KClass<T>) =
        mapper.tilEntry(this, clazz)

    private fun connect(client: RedisClient) =
        client.connect().apply {
            timeout = ofSeconds(cfg.timeout.seconds)
            if (isLocalOrTest) {
                sync().configSet("notify-keyspace-events", "Exd")
            }
        }
    private fun script() =
        ClassPathResource("scripts/count-all-keys.lua").getContentAsString(UTF_8)

    @PreDestroy
    fun closeConnections() {
        conn.close()
        batchConn.close()
    }
}
