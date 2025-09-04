package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING

@Service
class OppfølgingTjeneste(private val adapter: OppfølgingRestClientAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId.verdi")
    fun enhetFor(brukerId: BrukerId): Enhetsnummer? {
        // TODO implementer kall til oppfølging
        return null
    }
}





