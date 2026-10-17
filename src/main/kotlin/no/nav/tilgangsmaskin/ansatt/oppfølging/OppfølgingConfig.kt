package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.sikkerhetstjenesten.felles.cache.CachableRestConfig
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(OPPFØLGING)
class OppfølgingConfig : CachableRestConfig {
    override val navn = OPPFØLGING
    override val caches = setOf(OPPFØLGING_CACHE)

    companion object {
        const val OPPFØLGING = "oppfolging"
        val OPPFØLGING_CACHE = CacheNøkkelConfig(OPPFØLGING)
    }
}