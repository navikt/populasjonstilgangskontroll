package no.nav.tilgangsmaskin.ansatt.skjerming

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service


@RetryingWhenRecoverable
@Service
class SkjermingTjeneste(
    private val adapter: SkjermingRestClientAdapter,
    private val cache: CacheOperations,
    private val cf: SkjermingConfig,
) {

    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    @WithSpan
    fun skjerming(brukerId: BrukerId) = adapter.skjerming(brukerId.verdi)

    @WithSpan
    fun skjerminger(brukerIds: List<BrukerId>): Map<BrukerId, Boolean> {
        val ids = brukerIds.map { it.verdi }.toSet()
        val fraCache = fraCache(ids)
        log.trace("Hentet ${fraCache.size} skjerming(er) av ${ids.size} mulige fra CACHE")
        if (fraCache.size == ids.size) return fraCache.mapKeys { BrukerId(it.key) }.mapValues { it.value }

        val fraRest = adapter.skjerminger(ids - fraCache.keys)
        log.trace("Hentet ${fraRest.size} skjerming(er) av ${ids.size - fraCache.size} mulige fra REST")
        cache.putMany(SKJERMING_CACHE, fraRest, cf.varighet)
        return (fraRest + fraCache).mapKeys { BrukerId(it.key) }.mapValues { it.value }
    }

    private fun fraCache(ids: Set<String>) =
        cache.getMany(SKJERMING_CACHE, ids, Boolean::class)
            .filterValues { it != null }
            .mapValues { it.value!! }

    companion object {
        private val log = getLogger(SkjermingTjeneste::class.java)
    }
}

