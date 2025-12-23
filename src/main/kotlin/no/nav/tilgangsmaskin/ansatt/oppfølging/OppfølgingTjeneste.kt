package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class OppfølgingTjeneste(private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#id")
    fun enhetFor(id: String) =
        db.enhetFor(id)
}