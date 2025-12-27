package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.UUID


@ConfigurationProperties(NOM)
class NomConfig(val topic: String): CachableRestConfig {
    override val navn = NOM
    override val cacheNulls=true
    override val caches = setOf(NOM_CACHE)
    override val clazz = BrukerId::class


    companion object {
        const val NOM = "nom"
        val NOM_CACHE = CachableConfig(NOM)
    }
}
