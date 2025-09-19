package no.nav.tilgangsmaskin.ansatt.skjerming

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable


@RetryingOnRecoverableService

class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {


    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    @WithSpan
    fun skjerming(brukerId: BrukerId) = adapter.skjerming(brukerId.verdi)
    @WithSpan
    fun skjerminger(brukerIds: Set<BrukerId>) = adapter.skjerminger(brukerIds.map { it.verdi }.toSet())

}