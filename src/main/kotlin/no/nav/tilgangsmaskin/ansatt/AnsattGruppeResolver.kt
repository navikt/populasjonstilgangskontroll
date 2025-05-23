package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AnsattGruppeResolver(private val entra: Entra, private val token: Token) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun grupperForAnsatt(ansattId: AnsattId) =
        if (token.erObo) {
            val grupper = token.globaleGrupper()
            if (grupper.any { it.id == NASJONAL.id }) {
                log.info("OBO-flow: $ansattId har nasjonal tilgang, slår *ikke* opp GEO-grupper i Entra")
                grupper
            }
            else {
                log.info("OBO-flow: $ansattId har ikke nasjonal tilgang, slår opp GEO-grupper i Entra")
                grupper + entra.geoGrupper(ansattId)
            }
        }
        else {
            log.info("CC-flow: $ansattId slår opp globale og GEO-grupper i Entra")
            entra.geoOgGlobaleGrupper(ansattId)
        }
}