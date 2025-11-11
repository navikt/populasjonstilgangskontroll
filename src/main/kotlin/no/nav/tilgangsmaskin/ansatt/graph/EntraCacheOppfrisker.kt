package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import org.jboss.logging.MDC
import org.springframework.stereotype.Component

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste) : AbstractCacheOppfrisker() {

    override val cacheName: String = GRAPH

    override fun doOppfrisk(nøkkelElementer: CacheNøkkelElementer) {
        if (entra.erOppfriskbar) {
            val ansattId = AnsattId(nøkkelElementer.id)
            MDC.put(USER_ID, ansattId.verdi)
            when (nøkkelElementer.metode) {
                "geoOgGlobaleGrupper" -> entra.geoOgGlobaleGrupper(ansattId, oidTjeneste.oidFraEntra(ansattId))
                "geoGrupper" -> entra.geoGrupper(ansattId, oidTjeneste.oidFraEntra(ansattId))
                else -> error("Ukjent metode ${nøkkelElementer.metode} i nøkkel ${nøkkelElementer.nøkkel}")
            }
        }
    }
}