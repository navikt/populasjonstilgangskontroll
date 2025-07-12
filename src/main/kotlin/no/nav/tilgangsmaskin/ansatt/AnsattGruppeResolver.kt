package no.nav.tilgangsmaskin.ansatt

import no.nav.boot.conditionals.EnvUtil.isDevOrLocal
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter.Companion.requires
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.tilgang.Token.TokenType.CC
import no.nav.tilgangsmaskin.tilgang.Token.TokenType.OBO
import no.nav.tilgangsmaskin.tilgang.Token.TokenType.UNAUTHENTICATED
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component

@Component
class AnsattGruppeResolver(private val entra: EntraTjeneste, private val token: Token, private val env: Environment)  {

    private val log = getLogger(javaClass)

     fun grupperForAnsatt(ansattId: AnsattId) =
        when (token.type) {
            CC ->  grupperForCC(ansattId)
            OBO -> grupperForObo(ansattId)
            UNAUTHENTICATED -> {
                requires(isDevOrLocal(env),UNAUTHORIZED,   "Uautentisert oppslag kun støttet i dev")
                grupperForUautentisert(ansattId)
            }
        }

    private fun grupperForCC(ansattId: AnsattId) =
        entra.geoOgGlobaleGrupper(ansattId).also {
            log.info("CC-flow: $ansattId slo opp globale og GEO-grupper i Entra")
        }

    private fun grupperForObo(ansattId: AnsattId) = with(token.globaleGrupper()) {
        if (girNasjonalTilgang()) {
            this.also {
                log.info("OBO-flow: $ansattId har nasjonal tilgang, slo *ikke* opp GEO-grupper i Entra")
            }
        } else {
            (this + entra.geoGrupper(ansattId)).also {
                log.info("OBO-flow: $ansattId har ikke nasjonal tilgang, slo opp GEO-grupper i Entra")
            }
        }
    }
    private fun grupperForUautentisert(ansattId: AnsattId) =
           with(ansattId) {
               log.info("Intet token i dev for $this, slår opp globale og GEO-grupper i Entra")
               entra.geoOgGlobaleGrupper(this).also {
                   log.info("Uautentisert: $ansattId slo opp $it i Entra for $this")
               }
           }
}