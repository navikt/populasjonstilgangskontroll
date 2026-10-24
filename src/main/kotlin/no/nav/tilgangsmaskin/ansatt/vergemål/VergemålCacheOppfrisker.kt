package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import no.nav.sikkerhetstjenesten.felles.cache.AbstractCacheOppfrisker
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGE_CACHE
import org.springframework.stereotype.Component

@Component
class VergemålCacheOppfrisker(
    private val vergemål: VergemålTjeneste,
    cfg: VergemålConfig,
) : AbstractCacheOppfrisker(cfg) {
    override fun doOppfrisk(nøkkelElementer: CacheNøkkel) {
        vergemål.alle(AnsattId(nøkkelElementer.id))
    }

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [vergemål=$vergemål]"
}
