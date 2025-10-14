package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import org.springframework.stereotype.Component

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste) : AbstractCacheOppfrisker() {

    override val cacheName: String = GRAPH

    override fun doOppfrisk(nøkkelElementer: CacheNøkkelElementer) {
        val ansattId = AnsattId(nøkkelElementer.id)
        when (nøkkelElementer.metode) {
            "geoOgGlobaleGrupper" -> entra.geoOgGlobaleGrupper(ansattId, oidTjeneste.oidFraEntra(ansattId))
            "geoGrupper" -> entra.geoGrupper(ansattId, oidTjeneste.oidFraEntra(ansattId))
            else -> error("Ukjent metode ${nøkkelElementer.metode} i nøkkel ${nøkkelElementer.nøkkel}")
        }
    }
}