package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.NomConfig.Companion.OPPFØLGING
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class OppfølgingTjeneste(private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId")
    fun enhetFor(brukerId: String) =
        db.enhetFor(brukerId)
}