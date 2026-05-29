package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(OPPFØLGING)
@NoCoverageAnalysis
class OppfølgingConfig : CachableRestConfig {
    override val navn = OPPFØLGING
    override val caches = setOf(OPPFØLGING_CACHE)

    companion object {
        const val OPPFØLGING = "oppfolging"
        val OPPFØLGING_CACHE = CacheNøkkelConfig(OPPFØLGING)
    }
}