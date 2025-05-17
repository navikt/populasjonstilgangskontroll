package no.nav.tilgangsmaskin.felles.rest

interface CachableRestConfig {
    val initialCacheSize: Int
    val maxCacheSize: Int
    val expireHours: Long
    val navn: String
}