package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.core.NestedExceptionUtils.getMostSpecificCause
import org.springframework.retry.ExhaustedRetryException
import org.springframework.retry.annotation.Recover

@CacheableRetryingOnRecoverableService(cacheNames = [SKJERMING])
@Timed
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun skjerming(brukerId: BrukerId) = adapter.skjerming(brukerId.verdi)

    fun skjerminger(brukerId: List<BrukerId>) = adapter.skjerminger(brukerId.map { it.verdi })

    @Recover
    fun fallback(e: Throwable, brukerId: BrukerId) =
        when (e) {
            is RecoverableRestException -> true
            is ExhaustedRetryException -> throw getMostSpecificCause(e)
            else -> throw e
        }
}