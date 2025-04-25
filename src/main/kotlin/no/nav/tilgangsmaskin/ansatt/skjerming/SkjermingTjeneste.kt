package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService
import org.slf4j.LoggerFactory.getLogger

@CacheableRetryingOnRecoverableService(cacheNames = [SKJERMING])
@Timed
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    private val log = getLogger(javaClass)

    fun skjerming(brukerId: BrukerId) =
        /* if (brukerId.verdi.startsWith("030165")) throw RecoverableRestException(
                 HttpStatus.INTERNAL_SERVER_ERROR,
                 URI.create("https://www.vg.no")).also {
             log.warn("XXXXXX ${it.body}")
         }
         else */ adapter.skjerming(brukerId.verdi)

    fun skjerminger(brukerId: List<BrukerId>) = adapter.skjerminger(brukerId.map { it.verdi })

}