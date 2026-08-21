package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import java.time.Duration
import java.time.Duration.ofHours

@NoCoverageAnalysis
interface CachableRestConfig {
    val varighet: Duration get() = ofHours(12)
    val navn: String
    val cacheNulls: Boolean get() = false
    val caches: Set<CacheNøkkelConfig>
}