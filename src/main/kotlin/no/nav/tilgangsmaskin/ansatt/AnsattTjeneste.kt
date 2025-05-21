package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.grupperFraToken
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.entra.harNasjonalTilgang
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
class AnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                     private val brukere: BrukerTjeneste,
                     private val token: Token, private val teller: NasjonalGruppeTeller) {
    private val log = getLogger(javaClass)

    fun ansatt(ansattId: AnsattId) =
        with(grupperFraToken(token.globaleGruppeIds)) {
            if (harNasjonalTilgang()) {
                log.info("$ansattId har nasjonal tilgang, slår *ikke* opp GEO-grupper i Entra")
                teller.tell(Tags.of("medlem", true.toString()))
                ansattMedMedFamileOgGrupper(ansattId, this)
            } else {
                if (token.ansattId != null) {
                    log.info("OBO-flow: $ansattId har *ikke* av nasjonal tilgang, slår opp GEO-grupper i Entra")
                }
                else  {
                    log.trace("CC-flow: slår opp GEO-grupper i Entra")
                }
                 ansattMedMedFamileOgGrupper(ansattId, this + entra.grupper(ansattId)).also {
                     teller.tell(Tags.of("medlem", (it erMedlemAv NASJONAL).toString()))
                 }
            }
        }

    private fun ansattMedMedFamileOgGrupper(ansattId: AnsattId, grupper: Set<EntraGruppe>): Ansatt {
        val ansattBruker = ansatte.fnrForAnsatt(ansattId)?.let {
            runCatching {
                brukere.utvidetFamilie(it.verdi)
            }.getOrNull()
        }
        return Ansatt(ansattId, ansattBruker, grupper)
    }
}





