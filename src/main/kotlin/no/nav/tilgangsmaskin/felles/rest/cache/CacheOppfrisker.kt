package no.nav.tilgangsmaskin.felles.rest.cache

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(deler: CacheNøkkelDeler, id: String)
}