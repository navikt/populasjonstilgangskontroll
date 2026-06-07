package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.KClass

@Component
@Primary
class MeteredCacheOperations(
    private val delegate: ValkeyCacheOperations,
    private val alleTreffTeller: BulkCacheSuksessTeller,
    private val teller: BulkCacheTeller) : CacheOperations {

    private val log = getLogger(javaClass)

    override fun delete(cache: CacheNøkkelConfig, id: String) =
        delegate.delete(cache, id)

    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        delegate.getOne(cache, id, clazz)

    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) =
        delegate.putOne(cache, id, value, ttl)

    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> =
        delegate.getMany(cache, ids, clazz).also { result ->
            if (ids.isNotEmpty()) {
                tellOgLog(cache.name, result.size, ids.size)
            }
        }

    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) =
        delegate.putMany(cache, innslag, ttl)

    override fun clear(cache: CacheNøkkelConfig) =
        delegate.clear(cache)

    override fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long> =
        delegate.sizes(*caches)

    private fun tellOgLog(navn: String, funnet: Int, etterspurt: Int) {
        alleTreffTeller.tell(of("name", navn, "suksess", (funnet == etterspurt).toString()))
        teller.tell(of("cache", navn, "result", "miss"), etterspurt - funnet)
        teller.tell(of("cache", navn, "result", "hit"), funnet)
        log.trace("Fant {} verdier i cache {} for {} identer", funnet, navn, etterspurt)
    }
}

