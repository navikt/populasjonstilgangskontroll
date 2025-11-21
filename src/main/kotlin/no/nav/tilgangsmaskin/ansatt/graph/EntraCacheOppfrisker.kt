package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.regler.motor.OppfriskingTeller
import org.slf4j.MDC
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import java.util.*

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste, private val cache: CacheClient, private val teller: OppfriskingTeller) : AbstractCacheOppfrisker() {

    override val cacheName: String = GRAPH

    override fun doOppfrisk(elementer: CacheNøkkelElementer) {
        when (elementer.metode) {
            GEO -> oppfriskMedMetode(elementer,GEO)
            GEO_OG_GLOBALE -> oppfriskMedMetode(elementer,GEO_OG_GLOBALE)
            else -> log.warn("Ukjent metode ${elementer.metode} i nøkkel ${elementer.nøkkel}")
        }
    }

    private fun oppfriskMedMetode(elementer: CacheNøkkelElementer, metode: String) {
        val ansattId = AnsattId(elementer.id)
        MDC.put(USER_ID, ansattId.verdi)
        val oid  = oidTjeneste.oidFraEntra(ansattId)
        runCatching {
            invoke(metode, ansattId, oid)
        }.getOrElse {
            if (it is IrrecoverableRestException && it.statusCode == NOT_FOUND) {
                log.warn("Ansatt {} med oid {} ikke funnet i Entra, sletter og refresher cache entry", ansattId.verdi, oid)
                cache.delete(OID_CACHE,elementer.id)
                val nyoid = oidTjeneste.oidFraEntra(ansattId)
                log.info("Refresh oid OK for ansatt {}, ny verdi er {}", ansattId.verdi, nyoid)
                invoke(metode, ansattId, nyoid)
                teller.tell()
            }
            else {
                loggOppfriskingFeilet(elementer, it)
            }
        }
    }

    private fun invoke(metode: String, ansattId: AnsattId, oid: UUID) {
        when (metode) {
            GEO -> entra.geoGrupper(ansattId, oid)
            GEO_OG_GLOBALE -> entra.geoOgGlobaleGrupper(ansattId, oid)
            else -> log.warn("Ukjent metode $metode for ansatt ${ansattId.verdi} og oid $oid")
        }
    }

    companion object {
        private val OID_CACHE = CachableConfig(ENTRA_OID)
        private const val GEO = "geoGrupper"
        private const val GEO_OG_GLOBALE = "geoOgGlobaleGrupper"
    }
}