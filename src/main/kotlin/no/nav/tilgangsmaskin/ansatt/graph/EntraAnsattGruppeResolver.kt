package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.Companion.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GEO_OG_GLOBALE_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EntraAnsattGruppeResolver(private val entra: EntraTjeneste,
                                private val token: Token,
                                private val oid: EntraOidTjeneste,
                                private val cache: CacheOperations,
                                private val publisher: MessagePublisher) {

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
                val deleted = cache.delete(GEO_OG_GLOBALE_CACHE, ansattId.verdi)
                if (!deleted) {
                    publisher.publish(":warn: entra OID-problemer","Kunne ikke fjerne entra cache innslag for ${ansattId.verdi}")
                }
                publisher.publish(":warn: entra OID problemer", "${it.identifikator}, tømmer cache og prøver på nytt")
                val nyoid = oid.oid(ansattId)
                publisher.publish(":warn: OID endret til $nyoid", "${it.identifikator} ikke funnet, tømte cache og prøvde på nytt")
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

    private fun grupperForUautentisert(ansattId: AnsattId): Set<EntraGruppe> {
        check(!isProd) { "Autentisering påkrevet i produksjonsmiljøet" }
        log.info("Intet token i dev/local for {}, slår opp globale og GEO-grupper i Entra", ansattId)
        return entra.geoOgGlobaleGrupper(ansattId, oid.oid(ansattId))
    }
}
