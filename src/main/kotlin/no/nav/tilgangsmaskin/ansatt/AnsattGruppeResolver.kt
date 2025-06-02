package no.nav.tilgangsmaskin.ansatt

import no.nav.boot.conditionals.EnvUtil.isDevOrLocal
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.Entra
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

@Component
class AnsattGruppeResolver(private val entra: Entra, private val token: Token, private val env: Environment) {

    private val log = getLogger(javaClass)

    fun grupperForAnsatt(ansattId: AnsattId) =
        when {
            token.erCC ->  {
                log.info("CC-flow: $ansattId slår opp globale og GEO-grupper i Entra")
                entra.geoOgGlobaleGrupper(ansattId)
            }
            token.erObo ->  {
                with(token.globaleGrupper()) {
                    if (girNasjonalTilgang()) {
                        log.info("OBO-flow: $ansattId har nasjonal tilgang, slår *ikke* opp GEO-grupper i Entra")
                        this
                    }
                    else {
                        log.info("OBO-flow: $ansattId har ikke nasjonal tilgang, slår opp GEO-grupper i Entra")
                        this + entra.geoGrupper(ansattId)
                    }
                }
            }
            else -> {
                log.info("Intet token, IDTYP er ${token.idType}")
                if (isDevOrLocal(env)) {
                    log.info("Intet token: $ansattId slår opp globale og GEO-grupper i Entra")
                    entra.geoOgGlobaleGrupper(ansattId)
                }
                else {
                    log.warn("Intet token, slår *ikke* opp GEO-grupper i Entra")
                    throw HttpClientErrorException(UNAUTHORIZED, "Autentisering påkrevet i produksjonsmiljøet", HttpHeaders(), null, null)
                }
            }
       }

      private  fun Set<EntraGruppe>.girNasjonalTilgang() = any { it.id == NASJONAL.id }
}