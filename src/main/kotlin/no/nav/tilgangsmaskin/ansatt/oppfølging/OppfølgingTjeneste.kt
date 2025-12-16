package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OppfølgingTjeneste(private val adapter: OppfølgingRestClientAdapter, private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId")
    fun enhetFor(brukerId: String) =
        adapter.enheterFor(listOf(brukerId)).firstOrNull()?.enhet

    fun dbEnhetFor(brukerId: BrukerId) =
        db.finnEnhetFor(brukerId.verdi)

    fun slett(id: UUID) =
        db.slett(id)

    fun oppdater(id: UUID, kontor: Kontor) =
        db.oppdater(id, kontor.kontorId)


    fun start(hendelse: OppfølgingHendelse) =
        db.start(hendelse)
}