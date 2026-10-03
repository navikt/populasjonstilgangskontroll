package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import no.nav.sikkerhetstjenesten.felles.cache.AbstractCacheOppfrisker
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import org.springframework.stereotype.Component

@Component
class VergemålCacheOppfrisker(private val vergemål: VergemålTjeneste) : AbstractCacheOppfrisker() {
    override fun doOppfrisk(nøkkelElementer: CacheNøkkel) {
        vergemål.alle(AnsattId(nøkkelElementer.id))
    }

    override val cacheName = VERGEMÅL

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [vergemål=$vergemål]"
}