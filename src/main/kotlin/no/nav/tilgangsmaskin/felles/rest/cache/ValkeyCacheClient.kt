package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.api.StatefulRedisConnection
import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger

class ValkeyCacheClient(val handler: ValkeyCacheKeyHandler,
                        val conn: StatefulRedisConnection<String,String>,
                        val mapper: ObjectMapper,
                        val alleTreffTeller: BulkCacheSuksessTeller,
                        val teller: BulkCacheTeller)  {

    private val log = getLogger(javaClass)


    inline fun <reified T> getOne(cache: CacheConfig, id: String) =
        conn.sync().get(handler.toKey(cache,id))?.let { json ->
            mapper.readValue<T>(json)
        }

    inline fun <reified T> getMany(cache: CacheConfig, ids: Set<String>)  =
        if (ids.isEmpty()) {
            log.trace("Forespurt 0 id'er for cache ${cache.name}, returnerer tomt resultat")
            emptyMap()
        }
        else conn.sync()
            .mget(*ids.map {
                id -> handler.toKey(cache,id)}.toTypedArray<String>()
            )
            .filter {
                it.hasValue()
            }
            .associate {
                handler.fromKey(cache, it.key) to mapper.readValue<T>(it.value)
            }.also {
                alleTreffTeller.tell(of("name", cache.name, "suksess", (it.size == ids.size).toString()))
                teller.tell(of("cache", cache.name, "result", "miss"), ids.size - it.size)
                teller.tell(of("cache", cache.name, "result", "hit"), it.size)
                log.trace("Fant ${it.size} verdier i cache ${cache.name} for ${ids.size} id(er)")
            }

    fun putMany(cache: CacheConfig, innslag: Map<String, Any>, extraPrefix: String? = null) =
        if (innslag.isEmpty()) {
            log.trace("Skal legge til 0 verdier i cache ${cache.name}, gjør ingenting")
            "OK" 
        }
        else {
            conn.sync().mset(innslag
                .mapKeys { handler.toKey(cache,it.key) }
                .mapValues { mapper.writeValueAsString(it.value) }.also {
                    log.trace("Lager {} verdier for cache {} med prefix {}", it.values, cache.name, extraPrefix)
                })
        }

}