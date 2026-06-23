package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheOppfriskerTeller
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import org.springframework.stereotype.Component

@Component
class OppfølgingCacheOppfrisker(private val oppfølging: OppfølgingTjeneste,
                               teller: CacheOppfriskerTeller) : AbstractCacheOppfrisker(teller) {

    override val cacheName = OPPFØLGING

    override fun doOppfrisk(nøkkel: CacheNøkkel) =
        oppfølging.enhetFor(Identifikator(nøkkel.id))
}