package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(NOM)
class NomConfig: CachableRestConfig {
    override val navn = NOM
    override val cacheNulls=true
    override val caches = setOf(NOM_CACHE)

    companion object {
        const val NOM = "nom"
        val NOM_CACHE = CachableConfig(NOM)
    }
}