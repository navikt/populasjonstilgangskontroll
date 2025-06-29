package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable


@RetryingOnRecoverableService
@Timed
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    @Cacheable(cacheNames = [SKJERMING])
    fun skjerming(brukerId: BrukerId) = adapter.skjerming(brukerId.verdi)

    //@Cacheable(cacheNames = [SKJERMING], key = "#brukerIds.sorted()")
    fun skjerminger(brukerIds: Set<BrukerId>) = adapter.skjerminger(brukerIds.map { it.verdi }.toSet())

}