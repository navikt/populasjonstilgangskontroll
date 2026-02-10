package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import java.time.Duration

interface CachableRestConfig {
    val varighet: Duration get() = Duration.ofHours(12)
    val navn: String
    val cacheNulls: Boolean get() = false
     val caches: Set<CachableConfig>
}