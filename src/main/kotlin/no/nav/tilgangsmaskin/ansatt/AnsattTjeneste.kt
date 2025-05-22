package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.entra.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.nom.Nom
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.regler.motor.NasjonalGruppeTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL

@Service
@Timed
@ConditionalOnGCP
class AnsattTjeneste(private val entra: Entra,
                     private val ansatte: Nom,
                     private val brukere: BrukerTjeneste,
                     private val token: Token,
                     private val teller: NasjonalGruppeTeller) {
    private val log = getLogger(javaClass)

    fun ansatt(ansattId: AnsattId) =
        if (token.erObo) {
            val grupper = token.globaleGrupper()
            if (grupper.girNasjonalTilgang()) {
                ansatt(ansattId, grupper).also {
                    log.info("OBO-flow: $ansattId har nasjonal tilgang, slo *ikke* opp GEO-grupper i Entra")
                    tell( true)
                }
            }
            else {
                ansatt(ansattId, grupper + entra.geoGrupper(ansattId)).also {
                    log.info("OBO-flow: $ansattId har ikke nasjonal tilgang, slo opp GEO-grupper i Entra")
                    tell( false)
                }
            }
        }
        else {
            ansatt(ansattId, entra.geoOgGlobaleGrupper(ansattId)).also {
                log.info("CC-flow: $ansattId slo opp globale og GEO-grupper i Entra")
                tell (it erMedlemAv NASJONAL)
            }
        }


    private fun ansatt(ansattId: AnsattId, grupper: Set<EntraGruppe>): Ansatt {
        val ansattBruker = ansatte.fnrForAnsatt(ansattId)?.let {
            runCatching { brukere.utvidetFamilie(it.verdi) }.getOrNull()
        }
        return Ansatt(ansattId, ansattBruker, grupper)
    }

    private fun tell(status: Boolean) = teller.tell(Tags.of(MEDLEM,"$status"))

    companion object {
        private const val MEDLEM = "medlem"
    }
}





