package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.sikkerhetstjenesten.felles.cache.AbstractCacheOppfrisker
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_CACHE
import no.nav.tilgangsmaskin.bruker.Identifikator
import org.springframework.stereotype.Component

@Component
class OppfølgingCacheOppfrisker(
    cfg: OppfølgingConfig,
    private val oppfølging: OppfølgingTjeneste,
) : AbstractCacheOppfrisker(cfg) {
    override fun doOppfrisk(nøkkelElementer: CacheNøkkel) {
        oppfølging.enhetFor(Identifikator(nøkkelElementer.id))
    }
}
