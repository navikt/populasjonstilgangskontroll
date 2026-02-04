package no.nav.tilgangsmaskin.felles.cache

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(elementer: CacheNøkkelElementer)
}