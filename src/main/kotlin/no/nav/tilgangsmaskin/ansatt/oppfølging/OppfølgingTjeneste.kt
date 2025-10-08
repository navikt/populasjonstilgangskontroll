package no.nav.tilgangsmaskin.ansatt.oppfølging

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.Identifikator

@Service
class OppfølgingTjeneste(private val adapter: OppfølgingRestClientAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId.verdi")
    fun enheterFor(brukerId: List<Identifikator>) =
         adapter.enheterFor(brukerId.map { it.verdi })
}





