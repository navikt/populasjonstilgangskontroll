package no.nav.tilgangsmaskin.felles.rest.cache

import kotlin.reflect.KCallable

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(deler: CacheNøkkelDeler)
    fun valider(deler: CacheNøkkelDeler): KCallable<*>
}