package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.ansatt.oppfølging.NomConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(OPPFØLGING)
class NomConfig(val topic: String): CachableRestConfig {
    override val navn = OPPFØLGING
    override val cacheNulls=true
    override val caches = listOf(OPPFOLGING_CACHE)

    companion object {
        const val OPPFØLGING = "oppfolging"
        val OPPFOLGING_CACHE = CachableConfig(OPPFØLGING)
    }
}