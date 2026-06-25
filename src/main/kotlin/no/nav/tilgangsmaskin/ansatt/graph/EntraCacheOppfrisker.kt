package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste,
                           private val oidTjeneste: EntraOidTjeneste,
                           private val cache: CacheOperations,
                           private val teller: OIDEndringTeller) : AbstractCacheOppfrisker() {

    private val log = LoggerFactory.getLogger(javaClass)
    override val cacheName = EntraGrupperConfig.GRAPH

    override fun doOppfrisk(nøkkel: CacheNøkkel) {
        val ansattId = AnsattId(nøkkel.id)
        MDC.put(USER_ID, ansattId.verdi)
        val oid = oidTjeneste.oid(ansattId)
        runCatching {
            oppfriskFor(ansattId, oid, nøkkel.metode)
        }.getOrElse {
            if (it is NotFoundRestException) {
                tømOgOppfrisk(ansattId, oid, nøkkel.metode)
            } else {
                log.warn("Oppfrisking av ${nøkkel.maskert} feilet", it)
            }
        }
    }

    private fun tømOgOppfrisk(ansattId: AnsattId, oid: UUID, metode: String?) {
        log.warn("${ansattId.verdi} med oid $oid ikke funnet i Entra, sletter og oppfrisker cache-innslag")
        cache.delete(OID_CACHE, ansattId.verdi)
        with(oidTjeneste.oid(ansattId)) {
            log.info("Oppfrisking av oid OK for ${ansattId.verdi}, ny verdi er $this")
            oppfriskFor(ansattId, this, metode)
        }
        teller.tell()
    }

    private fun oppfriskFor(ansattId: AnsattId, oid: UUID, metode: String?) =
        when (metode) {
            GEO -> entra.geoGrupper(ansattId, oid)
            GEO_OG_GLOBALE -> entra.geoOgGlobaleGrupper(ansattId, oid)
            else -> log.warn("Ukjent metode $metode for ${ansattId.verdi} med oid $oid")
        }

    companion object {
        const val GEO = "geoGrupper"
        const val GEO_OG_GLOBALE = "geoOgGlobaleGrupper"
    }
}