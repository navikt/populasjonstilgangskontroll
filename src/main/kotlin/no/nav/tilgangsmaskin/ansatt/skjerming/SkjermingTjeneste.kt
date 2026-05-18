package no.nav.tilgangsmaskin.ansatt.skjerming

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.*
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable


@RetryingWhenRecoverableRestService
class SkjermingTjeneste(private val client: SkjermingClient, private val cache: CacheOperations, private val cf: SkjermingConfig) {

    private val log = getLogger(SkjermingTjeneste::class.java)

    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    @WithSpan
    fun skjerming(brukerId: BrukerId) = client.skjerming(mapOf(IDENT to brukerId.verdi))

    @WithSpan
    fun skjerminger(brukerIds: List<BrukerId>): Map<BrukerId, Boolean> {
        val ids = brukerIds.map { it.verdi }.toSet()
        val fraCache = fraCache(ids)
        log.trace("Hentet ${fraCache.size} skjerming(er) av ${ids.size} mulige fra $SKJERMING")
        if (fraCache.size == ids.size) return fraCache.mapKeys { BrukerId(it.key) }.mapValues { it.value }

        val fraRest = client.skjerminger(mapOf(IDENTER to (ids - fraCache.keys)))
        log.trace("Hentet ${fraRest.size} skjerming(er) av ${ids.size - fraCache.size} mulige fra REST")
        cache.putMany(SKJERMING_CACHE, fraRest, cf.varighet)
        return (fraRest + fraCache).mapKeys { BrukerId(it.key) }.mapValues { it.value }
    }

    private fun fraCache(ids: Set<String>) =
        cache.getMany<Boolean>(SKJERMING_CACHE, ids)
            .mapNotNull { (id, value) -> value?.let { id to it } }
            .toMap()

    private companion object {
        private const val IDENT = "personident"
        private const val IDENTER = IDENT + "er"
    }
}
