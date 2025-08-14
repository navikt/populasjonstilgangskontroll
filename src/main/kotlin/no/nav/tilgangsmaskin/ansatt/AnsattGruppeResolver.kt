package no.nav.tilgangsmaskin.ansatt

import no.nav.boot.conditionals.EnvUtil.isDevOrLocal
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

@Component
class AnsattGruppeResolver(private val entra: EntraTjeneste, private val token: Token, private val oid: AnsattOidTjeneste, private val env: Environment)  {

    private val log = getLogger(javaClass)

     fun grupperForAnsatt(ansattId: AnsattId) =
        when {
            token.erCC ->  grupperForCC(ansattId)
            token.erObo -> grupperForObo(ansattId)
            else -> grupperForUautentisert(ansattId)
        }

    private fun grupperForCC(ansattId: AnsattId) =
        entra.geoOgGlobaleGrupper(ansattId,oid.oidFraEntra(ansattId)).also {
            log.info("CC-flow: $ansattId slo opp globale og GEO-grupper i Entra")
        }

    private fun grupperForObo(ansattId: AnsattId) = with(token.globaleGrupper()) {
        if (girNasjonalTilgang()) {
            this.also {
                log.info("OBO-flow: $ansattId har nasjonal tilgang, slo *ikke* opp GEO-grupper i Entra")
            }
        } else {
            (this + entra.geoGrupper(ansattId, token.oid!!)).also {
                log.info("OBO-flow: $ansattId har ikke nasjonal tilgang, slo opp GEO-grupper i Entra")
            }
        }
    }
    private fun grupperForUautentisert(ansattId: AnsattId) =
        if (isDevOrLocal(env)) {
            log.info("Intet token i dev for $ansattId, slår opp globale og GEO-grupper i Entra")
            entra.geoOgGlobaleGrupper(ansattId,oid.oidFraEntra(ansattId)).also {
                log.info("Uautentisert: $ansattId slo opp $it i Entra for $ansattId")
            }
        } else {
            throw HttpClientErrorException(UNAUTHORIZED, "Autentisering påkrevet i produksjonsmiljøet", HttpHeaders(), null, null)
        }
}