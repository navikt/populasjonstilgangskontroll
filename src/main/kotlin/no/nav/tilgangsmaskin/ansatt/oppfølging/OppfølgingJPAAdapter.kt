package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository) {

    fun slett(id: UUID) =
        repository.deleteById(id)

    fun oppdater(id: UUID, enhetsnummer: String) =
        repository.updateKontorById(id,enhetsnummer)

    fun start(hendelse: OppfølgingHendelse) =
        repository.save(OppfølgingEntity(hendelse.oppfolgingsperiodeUuid).apply {
            brukerid = hendelse.ident.verdi
            aktoerid = hendelse.aktorId.verdi
            startTidspunkt = hendelse.startTidspunkt
            kontor = hendelse.kontor!!.kontorId.verdi
        })

    fun enhetFor(id: String) =
        repository.findByBrukerid(id)?.kontor?.let(::Enhetsnummer) ?:
        repository.findByAktoerid(id)?.kontor?.let(::Enhetsnummer)
}