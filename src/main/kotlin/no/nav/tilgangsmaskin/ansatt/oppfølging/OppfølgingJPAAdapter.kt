package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository) {

    fun avsluttOppfølging(id: UUID) =
        repository.deleteByUUID(id)
    fun oppdaterKontor(id: UUID, enhetsnummer: Enhetsnummer) =
        repository.updateKontorById(id,enhetsnummer.verdi)

    fun startOppfølging(id: UUID,brukerId: BrukerId, aktørId: AktørId, start: Instant, enhetsnummer: Enhetsnummer) =
        repository.save(OppfølgingEntity(id).apply {
            brukerid = brukerId.verdi
            aktoerid = aktørId.verdi
            startTidspunkt = start
            kontor = enhetsnummer.verdi
        })

    fun enhetFor(id: String) =
        repository.findByBrukerid(id)?.kontor?.let(::Enhetsnummer) ?:
        repository.findByAktoerid(id)?.kontor?.let(::Enhetsnummer)
}