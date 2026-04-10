package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import org.springframework.stereotype.Component

@Component
class VergemålCacheOppfrisker(private val vergemål: VergemålTjeneste) : AbstractCacheOppfrisker() {
    override fun doOppfrisk(elementer: CacheNøkkelElementer) {
        vergemål.vergemål(AnsattId(elementer.id))
    }

    override val cacheName = VERGEMÅL
}