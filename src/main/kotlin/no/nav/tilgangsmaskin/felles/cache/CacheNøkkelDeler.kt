package no.nav.tilgangsmaskin.felles.cache

data class CacheNøkkelDeler(val key: String) {
    private val deler = key.split("::", ":")
    val cacheName = deler.first()
    val metode = if (deler.size > 2) deler[1] else null
    val id = deler.last()
}