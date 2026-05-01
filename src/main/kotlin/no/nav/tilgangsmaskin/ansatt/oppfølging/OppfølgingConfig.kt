package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig

class OppfølgingConfig: CachableRestConfig {
    override val navn = OPPFØLGING
    override val caches = setOf(OPPFØLGING_CACHE)

    companion object {
        const val OPPFØLGING = "oppfolging"
        val OPPFØLGING_CACHE = CachableConfig(OPPFØLGING)
    }
}

