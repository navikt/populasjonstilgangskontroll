package no.nav.tilgangsmaskin.ansatt.skjerming

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.RetryingWhenRecoverable
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service


@RetryingWhenRecoverable
@Service
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {


    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    @WithSpan("skjermingtjeneste.skjerming")
    fun skjerming(brukerId: BrukerId) = adapter.skjerming(brukerId.verdi)
    @WithSpan("skjermingtjeneste.skjermingerbulk")
    fun skjerminger(brukerIds: List<BrukerId>) = adapter.skjerminger(brukerIds.map { it.verdi }.toSet())

}