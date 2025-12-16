package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class OppfølgingTjeneste(private val adapter: OppfølgingRestClientAdapter, private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId")
    fun enhetFor(brukerId: String) =
        adapter.enheterFor(listOf(brukerId)).firstOrNull()?.enhet

    fun dbEnhetFor(brukerId: BrukerId) =
        db.enhetFor(brukerId.verdi)
}