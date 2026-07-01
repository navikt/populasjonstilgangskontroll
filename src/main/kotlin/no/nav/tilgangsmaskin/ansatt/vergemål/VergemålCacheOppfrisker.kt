package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import org.springframework.stereotype.Component

@Component
class VergemålCacheOppfrisker(private val vergemål: VergemålTjeneste) : AbstractCacheOppfrisker() {
    override fun doOppfrisk(nøkkel: CacheNøkkel) {
        vergemål.alle(AnsattId(nøkkel.id))
    }

    override val cacheName = VERGEMÅL

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [vergemål=$vergemål]"
}