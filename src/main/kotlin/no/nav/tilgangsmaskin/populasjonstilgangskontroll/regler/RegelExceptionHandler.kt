package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.midlertidig.MidlertidigTilgangTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class RegelExceptionHandler(private val midlertidig: MidlertidigTilgangTjeneste)  {

    private val log = getLogger(RegelExceptionHandler::class.java)


    fun håndter(saksbehandlerId: NavId, brukerId: Fødselsnummer, e: Throwable) =
        if (e is RegelException && e.regel.erOverstyrbar && midlertidig.harMidlertidigTilgang(saksbehandlerId, brukerId)) {
            Unit.also { log.warn("Midlertidig tilgang er gitt til $brukerId av $saksbehandlerId") }
        } else {
            throw e
        }
}