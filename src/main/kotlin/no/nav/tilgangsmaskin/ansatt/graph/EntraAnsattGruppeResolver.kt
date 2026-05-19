package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

@Component
class EntraAnsattGruppeResolver(private val entra: EntraTjeneste,
                                private val token: Token,
                                private val oid: EntraOidTjeneste,
                                private val cache: CacheOperations) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun grupperForAnsatt(ansattId: AnsattId) =
        when {
            token.erCC -> grupperForCC(ansattId)
            token.erObo -> grupperForObo(ansattId)
            else -> grupperForUautentisert(ansattId)
        }

    private fun grupperForCC(ansattId: AnsattId) =
        runCatching {
            entra.geoOgGlobaleGrupper(ansattId, oid.oid(ansattId)).also {
                log.trace("CC-flow: {} slo opp globale og GEO-grupper i Entra", ansattId)
            }
        }.getOrElse {
            if (it is NotFoundRestException) {
                cache.delete(EntraGrupperConfig.GEO_OG_GLOBALE_CACHE, ansattId.verdi)
                val nyoid = oid.oid(ansattId)
                entra.geoOgGlobaleGrupper(ansattId, nyoid).also {
                    log.info("CC-flow: {} slo opp globale og GEO-grupper i Entra med ny oid {}", ansattId, nyoid)
                }
            } else {
                throw it
            }
        }

    private fun grupperForObo(ansattId: AnsattId) = with(token.globaleGrupper()) {
        if (girNasjonalTilgang()) {
            this.also {
                log.trace("OBO-flow: {} har nasjonal tilgang, slo *ikke* opp GEO-grupper i Entra", ansattId)
            }
        } else {
            (this + entra.geoGrupper(ansattId, token.oid!!)).also {
                log.trace("OBO-flow: {} har ikke nasjonal tilgang, slo opp GEO-grupper i Entra", ansattId)
            }
        }
    }

    private fun grupperForUautentisert(ansattId: AnsattId) =
        if (ClusterUtils.isProd) {
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                "Autentisering påkrevet i produksjonsmiljøet",
                HttpHeaders(),
                null,
                null)
        } else {
            log.info("Intet token i dev/local for $ansattId, slår opp globale og GEO-grupper i Entra")
            entra.geoOgGlobaleGrupper(ansattId, oid.oid(ansattId)).also {
                log.trace("Uautentisert i dev: {} slo opp {} i Entra for {}", ansattId, it, ansattId)
            }
        }
}
