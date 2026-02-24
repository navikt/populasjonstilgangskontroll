package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr

data class CacheNøkkelElementer(val nøkkel: String) {
    private val elementer = nøkkel.split("::", ":")
    val cacheName = elementer.first()
    val metode = if (elementer.size > 2) elementer[1] else null
    val id = elementer.last()
    val masked = "$cacheName::${id.maskFnr()}"
}
}