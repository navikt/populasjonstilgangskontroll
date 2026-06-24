package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheOppfriskerTeller
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import org.springframework.stereotype.Component

@Component
class VergemålCacheOppfrisker(private val vergemål: VergemålTjeneste,
                              teller: CacheOppfriskerTeller) : AbstractCacheOppfrisker(teller) {
    override fun doOppfrisk(nøkkel: CacheNøkkel) {
        vergemål.vergemål(AnsattId(nøkkel.id))
    }

    override val cacheName = VERGEMÅL

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [vergemål=$vergemål]"
}