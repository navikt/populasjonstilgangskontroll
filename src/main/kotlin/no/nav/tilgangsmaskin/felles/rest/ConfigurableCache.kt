package no.nav.tilgangsmaskin.felles.rest

import java.time.Duration

interface ConfigurableCache {
    val ttl: Duration get() = Duration.ofHours(12)
    val navn: String
    val cacheNulls: Boolean get() = false
}