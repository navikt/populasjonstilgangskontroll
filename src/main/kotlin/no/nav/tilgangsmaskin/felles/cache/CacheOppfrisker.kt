package no.nav.tilgangsmaskin.felles.cache

import kotlin.reflect.KCallable

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(deler: CacheNøkkelElementer)
    fun valider(deler: CacheNøkkelElementer): KCallable<*>
}