package no.nav.tilgangsmaskin.felles.rest

import java.time.Duration

interface CachableRestConfig {
    val varighet: Duration
    val navn: String
    val cacheNulls: Boolean get() = false
}