package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelMapper.CacheNøkkelElementer

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(elementer: CacheNøkkelElementer)
}