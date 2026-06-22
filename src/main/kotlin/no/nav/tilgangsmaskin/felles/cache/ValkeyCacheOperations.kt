package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.LettuceFutures.awaitAll
import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor.INITIAL
import io.lettuce.core.ScriptOutputType.MULTI
import io.opentelemetry.instrumentation.annotations.WithSpan
import jakarta.annotation.PreDestroy
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.CLEAR
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.DELETE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.GET_MANY
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.GET_ONE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.PUT_MANY
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Operasjon.PUT_ONE
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.FEILET
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.HIT
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.MISS
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheTeller.Resultat.OK
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isLocalOrTest
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8
import kotlin.time.measureTimedValue
import tools.jackson.databind.json.JsonMapper

@RetryingWhenRecoverableRestService
class ValkeyCacheOperations(
    client: RedisClient,
    private val cfg: CacheConfig,
    private val teller: ValkeyCacheTeller,
    private val valkeyMapper: JsonMapper,
) : CacheOperations {

    private val log = getLogger(javaClass)
    private val conn = connect(client)
    private val batchConn = connect(client)

    @WithSpan
    override fun delete(cache: CacheNøkkelConfig, id: String) =
        conn.sync().del(cache.tilNøkkel(id)).also {
            teller.tell(DELETE, cache.name, OK)
        }

    @WithSpan
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        runCatching {
            conn.sync().get(cache.tilNøkkel(id))?.let {
                valkeyMapper.readValue(it, clazz.java).also {
                    teller.tell(GET_ONE, cache.name, HIT)
                }
            } ?: run {
                teller.tell(GET_ONE, cache.name, MISS)
                null
            }
        }.getOrElse { e ->
            teller.tell(GET_ONE, cache.name, FEILET)
            log.info("Cache getOne feilet for {}, faller tilbake til tjenestekall", cache.name, e)
            null
        }

    @WithSpan
    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        conn.async().setex(cache.tilNøkkel(id), ttl.seconds, valkeyMapper.writeValueAsString(value))
        teller.tell(PUT_ONE, cache.name, OK)
    }

    @WithSpan
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>) =
        when {
            ids.isEmpty() -> emptyMap()
            ids.size == 1 -> doGetOne(cache, ids, clazz)
            else -> doGetMany(cache, ids, clazz)
        }

    @WithSpan
    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        when {
            innslag.isEmpty() -> return
            innslag.size == 1 -> doPutOne(cache, innslag, ttl)
            else -> doPutMany(cache, innslag, ttl.seconds)
        }
    }
    private fun <T : Any> doGetMany(cache: CacheNøkkelConfig,
                                    ids: Set<String>,
                                    clazz: KClass<T>): Map<String, T?> =
        measureTimedValue {
            runCatching {
                conn.sync()
                    .mget(*ids.map {
                        cache.tilNøkkel(it)
                    }.toTypedArray())
                    .filter {
                        it.hasValue()
                    }
                    .associate {
                        CacheNøkkel(it.key).id to valkeyMapper.readValue(it.value, clazz.java)
                    }
            }.getOrElse {
                teller.tell(GET_MANY, cache.name, FEILET, ids.size)
                log.info("{} getMany feilet for {} med {} nøkler: {}", javaClass.simpleName, cache.fullName, ids.size, it.message, it)
                emptyMap()
            }
        }.let {
            teller.tell(GET_MANY, cache.name, HIT, it.value.size)
            teller.tell(GET_MANY, cache.name, MISS, ids.size - it.value.size)
            log.info("getMany {} hentet {} av {} nøkler på {}ms", cache.fullName, it.value.size, ids.size,it.duration.inWholeMilliseconds)
            it.value
        }

    private fun <T : Any> doGetOne(cache: CacheNøkkelConfig,
                                   ids: Set<String>,
                                   clazz: KClass<T>): Map<String, T> =
        ids.single().let { id ->
        getOne(cache, id, clazz)?.let { mapOf(id to it) }.orEmpty()
    }




    override fun clear(cache: CacheNøkkelConfig) {
        check(!isProd) { "Clear er ikke støttet i prod for å unngå utilsiktet sletting av cache-innhold" }
        log.info("Tømmer cache {}", cache.name)
        val prefix = cache.tilNøkkel("")
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
        teller.tell(CLEAR, cache.name, OK, slettet.toInt())
    }

    override fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long> {
        val prefixes = caches.map {
            "${it.tilNøkkel("")}*"
        }.toTypedArray()
        val (results, totalDuration) = eval(*prefixes)
        return caches.zip(results).associate {
            (cache, count) -> cache.fullName to count
        }.also {
            log.info("Cache størrelser {} slått opp, tok {}ms", it, totalDuration.inWholeMilliseconds)
        }
    }

    private fun doPutOne(cache: CacheNøkkelConfig,
                         innslag: Map<String, Any>,
                         ttl: Duration) {
        with(innslag.entries.single()) {
            putOne(cache, key, value, ttl)
        }
    }

    private fun doPutMany(cache: CacheNøkkelConfig,
                          innslag: Map<String, Any>,
                          ttl: Long) {
        val payload = innslag.entries.associate { (key, value) ->
            cache.tilNøkkel(key) to valkeyMapper.writeValueAsString(value)
        }
        val (resultat, varighet) = measureTimedValue {
            flushBatch(payload, ttl)
        }
        resultat.onSuccess {
                teller.tell(PUT_MANY, cache.name, OK, innslag.size)
                log.info("Cache putMany {} lagret {} nøkler på {}ms",
                    cache.fullName,
                    innslag.size,
                    varighet.inWholeMilliseconds)
            }
            .onFailure {
                teller.tell(PUT_MANY, cache.name, FEILET, innslag.size)
                log.info("Cache putMany feilet for {} med {} nøkler: {}", cache.fullName, innslag.size, it.message, it)
            }
    }

    private fun flushBatch(payload: Map<String, String>, ttl: Long) =
        runCatching {
            val futures = synchronized(batchConn) {
                batchConn.setAutoFlushCommands(false)
                try {
                    payload.map { (key, value) ->
                        batchConn.async().setex(key, ttl, value)
                    }.also { batchConn.flushCommands() }
                } finally {
                    batchConn.setAutoFlushCommands(true)
                }
            }
            awaitAll(cfg.timeout, *futures.toTypedArray())
        }


    private fun eval(vararg prefixes: String) =
        measureTimedValue {
            conn.sync().eval<List<Long>>(SCRIPT, MULTI, emptyArray(), *prefixes)
        }

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
