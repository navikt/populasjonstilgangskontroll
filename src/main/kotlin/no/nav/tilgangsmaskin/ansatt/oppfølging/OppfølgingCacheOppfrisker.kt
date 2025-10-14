package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelHandler.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.CacheOppfrisker
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class OppfølgingCacheOppfrisker(private val oppfølging: OppfølgingTjeneste) : CacheOppfrisker {

    override val cacheName: String = OPPFØLGING
    private val log = getLogger(javaClass)

    override fun oppfrisk(nøkkelElementer: CacheNøkkelElementer) {
            runCatching {
                oppfølging.enhetFor(nøkkelElementer.id)
                log.info("Oppfrisking av ${nøkkelElementer.nøkkel} OK")
            }.getOrElse {
                log.info("Oppfrisking av ${nøkkelElementer.nøkkel} etter sletting feilet, dette er ikke kritisk",it)
            }
        }
}