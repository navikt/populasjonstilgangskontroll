package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable


@RetryingOnRecoverableService

class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    fun skjerming(brukerId: BrukerId) = adapter.skjerming(brukerId.verdi)

    fun skjerminger(brukerIds: Set<BrukerId>) = adapter.skjerminger(brukerIds.map { it.verdi }.toSet())

}