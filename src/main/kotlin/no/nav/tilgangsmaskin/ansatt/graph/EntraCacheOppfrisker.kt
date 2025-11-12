package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.regler.motor.OppfriskingTeller
import org.jboss.logging.MDC
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.getOrElse

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste, private val cache: CacheClient, private val teller: OppfriskingTeller) : AbstractCacheOppfrisker() {

    override val cacheName: String = GRAPH

    override fun doOppfrisk(elementer: CacheNøkkelElementer) {
        when (elementer.metode) {
            GEO -> oppfriskMedMetode(elementer,GEO)
            GEO_OG_GLOBALE -> oppfriskMedMetode(elementer,GEO_OG_GLOBALE)
            else -> error("Ukjent metode ${elementer.metode} i nøkkel ${elementer.nøkkel}")
        }
    }

    private fun oppfriskMedMetode(elementer: CacheNøkkelElementer, metode: String) {
        val ansattId = AnsattId(elementer.id)
        MDC.put(USER_ID, ansattId.verdi)
        val oid  = oidTjeneste.oidFraEntra(ansattId)
        runCatching {
            log.trace("Oppfrisk med ansatt ${ansattId.verdi}, id {} og metode {}", oid,metode)
            invoke(metode, ansattId, oid)
        }.getOrElse {
            if (it is IrrecoverableRestException && it.statusCode == NOT_FOUND) {
                log.info("Ansatt ${ansattId.verdi} med oid $oid ikke funnet i Entra, sletter og refresher cache entry ${elementer.nøkkel}")
                cache.delete("entraoid:: " + elementer.id)
                invoke(metode, ansattId, oidTjeneste.oidFraEntra(ansattId))
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
            else -> error("Ukjent metode $metode")
        }
    }

    companion object {
        private const val GEO = "geoGrupper"
        private const val GEO_OG_GLOBALE = "geoOgGlobaleGrupper"
    }
}