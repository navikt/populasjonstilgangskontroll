package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.Companion.girNasjonalTilgang
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.security.TokenType.CCF
import no.nav.tilgangsmaskin.felles.security.TokenType.OBO
import no.nav.tilgangsmaskin.felles.rest.notifikasjon.MessagePublisher
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EntraAnsattGruppeResolver(private val entra: EntraTjeneste,
                                private val authContext: AuthContext,
                                private val oid: EntraOidTjeneste,
                                private val cache: CacheOperations,
                                private val publisher: MessagePublisher) {

    private val log = getLogger(javaClass)

    fun grupperForAnsatt(ansattId: AnsattId) =
        when (authContext.type) {
            CCF -> grupperForCC(ansattId)
            OBO -> grupperForObo(ansattId)
            else -> grupperForUautentisert(ansattId)
        }

    private fun grupperForCC(ansattId: AnsattId) =
        runCatching {
            entra.geoOgGlobaleGrupper(ansattId, oid.oid(ansattId)).also {
                log.trace("CC-flow: {} slo opp globale og GEO-grupper i Entra", ansattId)
            }
        }.recoverCatching { e ->
            (e as? NotFoundRestException)?.let {
                notFound(ansattId, it)
            } ?: throw e
        }.getOrThrow()


    private fun notFound(ansattId: AnsattId,
                         e: NotFoundRestException): Set<EntraGruppe> {
        log.info("${ansattId.verdi} med oid ${e.identifikator} ikke funnet i Entra, sletter cache-innslag og prøver på nytt")
        val deleted = cache.delete(OID_CACHE, ansattId.verdi)
        if (!deleted) {
            publisher.warn("Entra OID-problemer",
                "Kunne ikke fjerne entra cache innslag for ${ansattId.verdi} og oid ${e.identifikator}")
        }
        val nyoid = oid.oid(ansattId)
        log.warn("OID for $ansattId endret fra ${e.identifikator} til  $nyoid for $ansattId, ${e.identifikator} ikke funnet, tømte cache og prøvde på nytt")
        publisher.warn("OID for $ansattId endret fra ${e.identifikator} til $nyoid","prøver på nytt med ny oid")
        runCatching {
            return entra.geoOgGlobaleGrupper(ansattId, nyoid).also {
                log.info("{} slo opp globale og GEO-grupper i Entra med ny oid {}", ansattId, nyoid)
            }
            }.onFailure { e ->
                publisher.warn("Entra OID-problemer",
                    "Kunne ikke slå opp globale og GEO-grupper i Entra med ny oid $nyoid")
                log.error("Kunne ikke slå opp globale og GEO-grupper i Entra med ny oid $nyoid",e)
        }.getOrThrow()
    }

    private fun grupperForObo(ansattId: AnsattId) = with(authContext.globaleGrupper()) {
        if (girNasjonalTilgang()) {
            this.also {
                log.trace("OBO-flow: {} har nasjonal tilgang, slo *ikke* opp GEO-grupper i Entra", ansattId)
            }
        } else {
            (this + entra.geoGrupper(ansattId, authContext.oid!!)).also {
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
