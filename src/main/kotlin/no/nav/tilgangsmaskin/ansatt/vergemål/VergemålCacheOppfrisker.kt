package no.nav.tilgangsmaskin.ansatt.vergemål

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import org.springframework.stereotype.Component

@Component
class VergemålCacheOppfrisker(private val vergemål: VergemålTjeneste) : AbstractCacheOppfrisker() {
    @Timed("jallajalla")
    override fun doOppfrisk(nøkkel: CacheNøkkel) {
        vergemål.vergemål(AnsattId(nøkkel.id))
    }

    override val cacheName = VERGEMÅL

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [vergemål=$vergemål]"
}