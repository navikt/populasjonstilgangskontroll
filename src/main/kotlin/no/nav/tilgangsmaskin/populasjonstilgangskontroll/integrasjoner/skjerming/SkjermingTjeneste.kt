package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Recover
import kotlin.arrayOf

@RetryingOnRecoverableCacheableService(cacheNames = [SKJERMING])
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    private val log = LoggerFactory.getLogger(SkjermingTjeneste::class.java)

    fun erSkjermet(brukerId: BrukerId) = adapter.erSkjermet(brukerId.verdi)

    @Recover
    fun fallback(e: RecoverableRestException, brukerId: BrukerId) = false.also {
        log.warn("Recovery fallback: Kunne ikke hente skjerming for bruker ${brukerId.verdi} ", e)
    }
}