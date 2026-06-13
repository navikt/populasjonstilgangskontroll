package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.LettuceFutures.awaitAll
import io.lettuce.core.KeyValue
import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor.INITIAL
import io.lettuce.core.ScriptOutputType.MULTI
import io.opentelemetry.instrumentation.annotations.WithSpan
import jakarta.annotation.PreDestroy
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.clear
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.delete
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.getMany
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.getOne
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.putMany
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.putOne
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.feilet
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.hit
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.miss
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.ok
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.collections.emptyMap
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.measureTimedValue

@RetryingWhenRecoverableRestService
class ValkeyCacheOperations(client: RedisClient,
                            private val mapper: CacheNøkkelMapper,
                            private val teller: ValkeyCacheTeller,
                            private val cfg: CacheConfig) : CacheOperations {

    private val log = getLogger(javaClass)
    private val conn = connect(client)
    private val batchConn = connect(client)

    @WithSpan
    override fun delete(cache: CacheNøkkelConfig, id: String) =
        conn.sync().del(mapper.tilNøkkel(cache, id))
            .also {
                teller.tell(delete, cache.name, ok)
            }

    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        runCatching {
            conn.sync().get(mapper.tilNøkkel(cache, id))?.let { json ->
                mapper.fraJson(json, clazz)
                    .also { teller.tell(getOne, cache.name, hit) }
            } ?: run {
                teller.tell(getOne, cache.name, miss)
                null
            }
        }.getOrElse { ex ->
            teller.tell(getOne, cache.name, feilet)
            log.info("Cache getOne feilet for {}, faller tilbake til tjenestekall", cache.name, ex)
            null
        }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        conn.async().setex(mapper.tilNøkkel(cache, id), ttl.seconds, mapper.tilJson(value))
        teller.tell(putOne, cache.name, ok)
    }

    @WithSpan
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>) =
        when {
            ids.isEmpty() -> emptyMap()
            ids.size == 1 -> ids.single().let { id ->
                getOne(cache, id, clazz)?.let { mapOf(id to it) }.orEmpty()
            }
            else -> {
                val (result, elapsed) = measureTimedValue {
                    runCatching {
                        val keys = ids.map { mapper.tilNøkkel(cache, it) }.toTypedArray()
                        conn.sync()
                            .mget(*keys)
                            .filter { it.hasValue() }
                            .associate { it.fraJsonEntry(mapper, clazz) }
                    }.getOrElse {
                        teller.tell(getMany, cache.name, feilet, ids.size)
                        log.info("Cache getMany feilet for ${cache.fullName} med ${ids.size} nøkler, faller tilbake til tjenestekall: ${it.message}", it)
                        emptyMap()
                    }
                }
                result.also {
                    teller.tell(getMany, cache.name, hit, it.size)
                    teller.tell(getMany, cache.name, miss, ids.size - it.size)
                    log.info("getMany {} hentet {} av {} nøkler på {}ms", cache.fullName, it.size, ids.size, elapsed.inWholeMilliseconds)
                }
            }
        }

    @WithSpan
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        when {
            innslag.isEmpty() -> return
            innslag.size == 1 -> with(innslag.entries.single()) { putOne(cache, key, value, ttl) }
            else -> {
                val (resultat, varighet) = measureTimedValue { flushBatch(payload(innslag, cache), ttl) }
                resultat
                    .onSuccess {
                        teller.tell(putMany, cache.name, ok, innslag.size)
                        log.info("Cache putMany {} lagret {} nøkler på {}ms", cache.fullName, innslag.size, varighet.inWholeMilliseconds)
                    }
                    .onFailure {
                        teller.tell(putMany, cache.name, feilet, innslag.size)
                        log.info("Cache putMany feilet for ${cache.fullName} med ${innslag.size} nøkler: ${it.message}", it)
                    }
            }
        }
    }

    private fun flushBatch(payload: Map<String, String>, ttl: Duration) =
        runCatching {
            val futures = synchronized(batchConn) {
                batchConn.setAutoFlushCommands(false)
                try {
                    payload.map { (key, value) ->
                        batchConn.async().setex(key, ttl.seconds, value)
                    }.also {
                        batchConn.flushCommands()
                    }
                } finally {
                    batchConn.setAutoFlushCommands(true)
                }
            }
            awaitAll(cfg.timeout, *futures.toTypedArray())
        }

    private fun payload(innslag: Map<String, Any>, cache: CacheNøkkelConfig): Map<String, String> =
        innslag.entries.associate { (key, value) -> mapper.tilJsonEntry(cache, key, value) }

    override fun tilNøkkel(cache: CacheNøkkelConfig, id: String) = mapper.tilNøkkel(cache, id)

    override fun clear(cache: CacheNøkkelConfig) {
        check(!isProd) {
            "Clear er ikke støttet i prod for å unngå utilsiktet sletting av cache-innhold"
        }
        log.info("Tømmer cache {}", cache.name)
        val prefix = mapper.tilNøkkel(cache, "")
        var cursor = INITIAL
        val args = ScanArgs().match("$prefix*").limit(10000)
        var slettet = 0L
        do {
            val result = conn.sync().scan(cursor, args)
            if (result.keys.isNotEmpty()) {
                slettet += conn.sync().del(*result.keys.toTypedArray())
            }
            cursor = result
        } while (!result.isFinished)
        teller.tell(clear, cache.name, ok, slettet.toInt())
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
            conn.sync().eval<List<Long>>(SCRIPT, MULTI, emptyArray(), *prefixes)
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


    @PreDestroy
    fun closeConnections() {
        conn.close()
        batchConn.close()
    }

    companion object {
        private val SCRIPT = ClassPathResource("scripts/count-all-keys.lua").getContentAsString(UTF_8)
    }
}
