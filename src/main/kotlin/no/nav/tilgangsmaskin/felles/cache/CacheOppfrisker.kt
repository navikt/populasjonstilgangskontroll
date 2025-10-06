package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelHandler.CacheNøkkelElementer

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(elementer: CacheNøkkelElementer)
}