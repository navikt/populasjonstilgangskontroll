package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.grupperFraToken
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.entra.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.nom.Nom
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.regler.motor.NasjonalGruppeTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
@ConditionalOnGCP
class AnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                     private val brukere: BrukerTjeneste,
                     private val token: Token, private val teller: NasjonalGruppeTeller) {
    private val log = getLogger(javaClass)

    fun ansatt(ansattId: AnsattId) =
        with(grupperFraToken(token.globaleGruppeIds)) {
            if (girNasjonalTilgang()) {
                log.info("$ansattId har tilgang til nasjonal gruppe, slår ikke opp i Entra for GEO-grupper")
                teller.tell(Tags.of("medlem", true.toString()))
                ansattMedMedFamileOgGrupper(ansattId, this)
            } else {
                log.info("$ansattId har *ikke* tilgang til nasjonal gruppe, slår opp i Entra for GEO-grupper")
                teller.tell(Tags.of("medlem", false.toString()))
                ansattMedMedFamileOgGrupper(ansattId, this + entra.grupper(ansattId))
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





