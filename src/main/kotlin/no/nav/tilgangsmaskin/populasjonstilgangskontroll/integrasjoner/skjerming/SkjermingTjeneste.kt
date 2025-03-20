package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.core.NestedExceptionUtils
import org.springframework.retry.ExhaustedRetryException
import org.springframework.retry.annotation.Recover

@CacheableRetryingOnRecoverableService(cacheNames = [SKJERMING])
@Timed
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun erSkjermet(brukerId: BrukerId)  =  adapter.erSkjermet(brukerId.verdi)

    @Recover
    fun fallback(e: Throwable, brukerId: BrukerId) =
        when (e) {
            is RecoverableRestException -> true
            is ExhaustedRetryException -> throw NestedExceptionUtils.getMostSpecificCause(e)
            else -> throw e
    }
}