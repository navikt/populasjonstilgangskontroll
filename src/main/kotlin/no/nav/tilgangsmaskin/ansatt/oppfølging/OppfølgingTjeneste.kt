package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OppfølgingTjeneste(private val adapter: OppfølgingRestClientAdapter, private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId")
    fun enhetFor(brukerId: String) =
        adapter.enheterFor(listOf(brukerId)).firstOrNull()?.enhet

    fun slett(id: UUID) =
        db.slett(id)

    fun oppdater(id: UUID, kontor: Kontor) =
        db.oppdater(kontor.kontorId, id)


    fun start(hendelse: OppfølgingHendelse) =
        db.oppdater(kontor.kontorId, id)
}