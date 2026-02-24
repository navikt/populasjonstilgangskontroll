package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.regler.motor.OppfriskingTeller
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste, private val cache: CacheClient, private val teller: OppfriskingTeller) : AbstractCacheOppfrisker() {

    override val cacheName: String = GRAPH

    override fun doOppfrisk(elementer: CacheNøkkelElementer)  =
        oppfriskMedMetode(elementer, elementer.metode)

    private fun oppfriskMedMetode(elementer: CacheNøkkelElementer, metode: String?) {
        val ansattId = AnsattId(elementer.id)
        MDC.put(USER_ID, ansattId.verdi)
        val oid  = oidTjeneste.oidFraEntra(ansattId)
        runCatching {
            oppfrisk(ansattId, oid, metode)
        }.getOrElse {
            if (it is NotFoundRestException) {
                tømOgOppfrisk(ansattId, oid, metode)
            }
            else {
                feil(elementer, it)
            }
        }
    }

    private fun tømOgOppfrisk(ansattId: AnsattId, oid: UUID, metode: String?) {
        log.warn("${ansattId.verdi} med oid $oid ikke funnet i Entra, sletter og oppfrisker cache innslag")
        cache.delete(OID_CACHE, ansattId.verdi)
        val nyoid = oidTjeneste.oidFraEntra(ansattId)
        log.info("Oppfrisking oid OK for ${ansattId.verdi}, ny verdi er $nyoid")
        oppfrisk(ansattId, nyoid, metode)
        teller.tell()
    }

    private fun oppfrisk(ansattId: AnsattId, oid: UUID, metode: String?) {
        when (metode) {
            GEO -> entra.geoGrupper(ansattId, oid)
            GEO_OG_GLOBALE -> entra.geoOgGlobaleGrupper(ansattId, oid)
            else -> log.warn("Ukjent metode $metode for ${ansattId.verdi} med oid $oid")
        }
    }

    companion object {
        private const val GEO = "geoGrupper"
        private const val GEO_OG_GLOBALE = "geoOgGlobaleGrupper"
    }
}