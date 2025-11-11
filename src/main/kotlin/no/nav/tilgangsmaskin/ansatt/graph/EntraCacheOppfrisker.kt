package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import org.jboss.logging.MDC
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.getOrElse

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste, private val cache: CacheClient) : AbstractCacheOppfrisker() {

    override val cacheName: String = GRAPH

    override fun doOppfrisk(nøkkelElementer: CacheNøkkelElementer) {
        MDC.put(USER_ID, nøkkelElementer.id)
        when (val metode = nøkkelElementer.metode) {
            "geoGrupper","geoOgGlobaleGrupper" -> oppfriskMedMetode(nøkkelElementer,metode)
            else -> error("Ukjent metode ${nøkkelElementer.metode} i nøkkel ${nøkkelElementer.nøkkel}")
        }
    }
    private fun oppfriskMedMetode(nøkkelElementer: CacheNøkkelElementer, metode: String) {
        runCatching {
            val ansattId = AnsattId(nøkkelElementer.id)
            val oid = oid(ansattId, nøkkelElementer.nøkkel)
            EntraTjeneste::class.java.getMethod(metode, AnsattId::class.java, UUID::class.java)
                .invoke(entra, ansattId, oid)
        }.getOrElse {
           loggOppfriskingFeilet(nøkkelElementer, it)
        }
    }
    private fun oid(id: AnsattId, nøkkel: String) =
        runCatching {
            oidTjeneste.oidFraEntra(id)
        }.getOrElse {
            if (it is IrrecoverableRestException && it.statusCode == NOT_FOUND) {
                cache.deleteOne(nøkkel)
                oidTjeneste.oidFraEntra(id)
                log.info("Oppfrisking av OID for ansattId=$id etter 404 OK")
            } else {
                throw it
            }
        }
}