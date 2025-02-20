package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class RegelExceptionHandler(private val overstyring: OverstyringTjeneste)  {

    private val log = getLogger(RegelExceptionHandler::class.java)

    fun håndter(ansattId: NavId, brukerId: Fødselsnummer, e: Throwable) =
        when (e) {
            is RegelException -> {
                with(e.regel) {
                    if (erOverstyrbar) {
                        if (overstyring.erOverstyrt(ansattId, brukerId)) {
                            log.warn("Overstyrt tilgang for regel '${beskrivelse.kortNavn}' er gitt til ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                        }
                        else {
                            throw e.also { log.warn("Tilgang avvist av regel '${beskrivelse.kortNavn}' for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'") }
                        }
                    } else {
                        throw e.also { log.warn("Tilgang avvist av regel '${beskrivelse.kortNavn}' for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}', regel er ikke overstyrbar") }
                    }
                }
            }
            else -> throw e.also { log.error("Ukjent feil ved tilgangskontroll for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'", it) }
        }
}