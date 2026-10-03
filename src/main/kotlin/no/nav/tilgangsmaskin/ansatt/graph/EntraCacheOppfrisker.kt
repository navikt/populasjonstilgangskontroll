package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.sikkerhetstjenesten.felles.cache.AbstractCacheOppfrisker
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkel
import no.nav.sikkerhetstjenesten.felles.cache.CacheOperations
import no.nav.sikkerhetstjenesten.felles.domain.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.sikkerhetstjenesten.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.sikkerhetstjenesten.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste,
                           private val oidTjeneste: EntraOidTjeneste,
                           private val cache: CacheOperations) : AbstractCacheOppfrisker() {

    override val cacheName = EntraGrupperConfig.GRAPH

    override fun doOppfrisk(nøkkelElementer: CacheNøkkel) {
        val ansattId = AnsattId(nøkkelElementer.id)
        withMDC(USER_ID to ansattId.verdi) {
            val oid = oidTjeneste.oid(ansattId)
            runCatching {
                oppfriskFor(ansattId, oid, nøkkelElementer.metode)
            }.recoverCatching { e ->
                (e as? NotFoundRestException)?.let {
                    tømOgOppfrisk(ansattId, oid, nøkkelElementer.metode)
                } ?: log.info(
                    "Oppfrisking av ${nøkkelElementer.cacheName}::${nøkkelElementer.metode}:${nøkkelElementer.id.maskFnr()} feilet",
                    e,
                )
            }.getOrThrow()
        }
    }

    private fun tømOgOppfrisk(ansattId: AnsattId, oid: UUID, metode: String?) {
        log.warn("${ansattId.verdi} med oid $oid ikke funnet i Entra, sletter og oppfrisker cache-innslag")
        cache.delete(OID_CACHE, ansattId.verdi)
        with(oidTjeneste.oid(ansattId)) {
            log.info("Oppfrisking av oid OK for ${ansattId.verdi}, ny verdi er $this")
            oppfriskFor(ansattId, this, metode)
        }
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