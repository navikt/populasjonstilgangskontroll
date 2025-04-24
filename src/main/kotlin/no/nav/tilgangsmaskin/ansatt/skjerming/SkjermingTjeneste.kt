package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.annotation.Timed
import java.net.URI
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.http.HttpStatus

@CacheableRetryingOnRecoverableService(cacheNames = [SKJERMING])
@Timed
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun skjerming(brukerId: BrukerId) =
        if (brukerId.verdi.startsWith("030165")) throw RecoverableRestException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                URI.create("https://www.vg.no")) else adapter.skjerming(brukerId.verdi)

    fun skjerminger(brukerId: List<BrukerId>) = adapter.skjerminger(brukerId.map { it.verdi })

}