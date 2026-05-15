package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.ENTRA_BASE_URI
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidClient.Companion.ENTRA_PING_PATH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class EntraOidConfig : CachableRestConfig, RestConfig(ENTRA_BASE_URI, ENTRA_PING_PATH, GRAPH) {
    override val navn     = ENTRA_OID
    override val varighet = Duration.ofDays(365)
    override val caches   = setOf(OID_CACHE)

    companion object {
        const val ENTRA_OID = "entraoid"
        val OID_CACHE = CacheNøkkelConfig(ENTRA_OID)
    }
}