package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository) {

    fun lagre(oppfølging: OppfølgingHendelse) = repository.save(OppfølgingEntity(oppfølging.oppfolgingsperiodeUuid).apply {
        brukerid = oppfølging.ident.verdi
        aktoerid = oppfølging.aktorId.verdi
        startTidspunkt = oppfølging.startTidspunkt
        kontor = oppfølging.kontor!!.kontorId.verdi
        sluttTidspunkt = oppfølging.sluttTidspunkt
    })

    fun slett(id: UUID) =
        repository.deleteById(id)

    fun oppdater(id: UUID, enhetsnummer: Enhetsnummer) =
        repository.updateKontorById(id,enhetsnummer.verdi)

    fun start(hendelse: OppfølgingHendelse) =
        repository.save(OppfølgingEntity(hendelse.oppfolgingsperiodeUuid).apply {
            brukerid = hendelse.ident.verdi
            aktoerid = hendelse.aktorId.verdi
            startTidspunkt = hendelse.startTidspunkt
            kontor = hendelse.kontor!!.kontorId.verdi
            sluttTidspunkt = hendelse.sluttTidspunkt
        })
}