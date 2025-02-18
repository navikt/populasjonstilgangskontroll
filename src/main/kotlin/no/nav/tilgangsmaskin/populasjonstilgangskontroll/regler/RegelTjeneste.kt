package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.midlertidig.MidlertidigTilgangTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val midlertidig: MidlertidigTilgangTjeneste)  {

    private val log = getLogger(RegelTjeneste::class.java)

    fun sjekkTilgang(saksbehandlerId: NavId, brukerId: Fødselsnummer) =
        runCatching {
            motor.vurderTilgang(brukerTjeneste.bruker(brukerId), ansattTjeneste.ansatt(saksbehandlerId))
        }.getOrElse {
            if (it is RegelException && it.regel.erOverstyrbar && midlertidig.harMidlertidigTilgang(saksbehandlerId, brukerId)) {
                Unit.also {
                    log.info("Midlertidig tilgang er gitt for saksbehandler {} for bruker {}", saksbehandlerId, brukerId)
                }
            } else {
                throw it.also {
                    log.error("Feil ved tilgangssjekk for saksbehandler {} for bruker {}", saksbehandlerId, brukerId, it)
                }
            }
        }
}

