package no.nav.tilgangsmaskin.ansatt.oppfølging

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Identifikator

@Service
class OppfølgingTjeneste(private val adapter: OppfølgingRestClientAdapter) {


    fun enhetFor(brukerId: String) =
        adapter.enheterFor(listOf(brukerId)).firstOrNull()?.enhet
   // @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId.verdi")
    fun enheterFor(brukerIds: List<String>) =
         adapter.enheterFor(brukerIds).map { it.enhet }
}





