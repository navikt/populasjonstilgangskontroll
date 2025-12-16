package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository) {

    fun lagre(oppfølging: OppfølgingHendelse) = repository.save(OppfølgingEntity(oppfølging.oppfolgingsperiodeUuid).apply {
        brukerid = oppfølging.ident.verdi
        aktoerid = oppfølging.aktorId.verdi
        startTidspunkt = oppfølging.startTidspunkt
        kontor = oppfølging.kontor.kontorId.verdi
        sluttTidspunkt = oppfølging.sluttTidspunkt
    })

    fun slett(id: UUID) =
        repository.deleteById(id)
}