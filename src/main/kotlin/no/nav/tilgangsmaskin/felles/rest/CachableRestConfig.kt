package no.nav.tilgangsmaskin.felles.rest

interface CachableRestConfig {
    val expireHours: Long
    val navn: String
}