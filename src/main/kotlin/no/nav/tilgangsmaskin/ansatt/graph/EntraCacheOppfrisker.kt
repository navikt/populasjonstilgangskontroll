package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.regler.motor.OppfriskingTeller
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC.put
import org.springframework.stereotype.Component
import java.util.*

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste, private val cache: CacheClient, private val teller: OppfriskingTeller) : AbstractCacheOppfrisker() {

    private val log = getLogger(javaClass)
    override val cacheName = GRAPH

    override fun doOppfrisk(nøkkel: CacheNøkkel) {
        val ansattId = AnsattId(nøkkel.id)
        put(USER_ID, ansattId.verdi)
        val oid = oidTjeneste.oidFraEntra(ansattId)
        this.runCatching {
            oppfriskFor(ansattId, oid, nøkkel.metode)
        }.getOrElse {
            if (it is NotFoundRestException) {
                tømOgOppfrisk(ansattId, oid, nøkkel.metode)
            } else {
                log.warn("Oppfrisking av ${nøkkel.masked} feilet", it)
            }
        }
    }

    private fun tømOgOppfrisk(ansattId: AnsattId, oid: UUID, metode: String?) {
        log.warn("${ansattId.verdi} med oid $oid ikke funnet i Entra, sletter og oppfrisker cache innslag")
        cache.delete(OID_CACHE, ansattId.verdi)
        with(oidTjeneste.oidFraEntra(ansattId)) {
            log.info("Oppfrisking oid OK for ${ansattId.verdi}, ny verdi er $this")
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