package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.RecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.core.NestedExceptionUtils.getMostSpecificCause
import org.springframework.retry.ExhaustedRetryException
import org.springframework.retry.annotation.Recover

@CacheableRetryingOnRecoverableService(cacheNames = [SKJERMING])
@Timed
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun skjerming(brukerId: BrukerId)  =  adapter.skjerming(brukerId.verdi)

    fun skjerminger(brukerId: List<BrukerId>)  =  adapter.skjerminger(brukerId.map { it.verdi })

    @Recover
    fun fallback(e: Throwable, brukerId: BrukerId) =
        when (e) {
            is RecoverableRestException -> true
            is ExhaustedRetryException -> throw getMostSpecificCause(e)
            else -> throw e
    }
}