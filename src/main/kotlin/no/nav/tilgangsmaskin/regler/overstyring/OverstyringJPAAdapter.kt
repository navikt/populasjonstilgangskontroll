package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.stereotype.Component
import java.time.ZoneId.systemDefault

@Component
class OverstyringJPAAdapter(private val repository: OverstyringRepository) {

    fun overstyr(ansattId: String, enhetsnummer: String, data: OverstyringData) =
        with(data) {
            repository.save(OverstyringEntity(ansattId, brukerId.verdi, begrunnelse, enhetsnummer,gyldigtil.atStartOfDay(systemDefault()).toInstant()))
            Unit
        }

    fun gjeldendeOverstyring(ansattId: String, brukerId: String, brukerIds: List<String>) =
        repository.gjeldendeOverstyring(ansattId, brukerId, brukerIds)

    fun gjeldendeOverstyringer(ansattId: String,  brukerIds: List<String>) =
        repository.gjeldendeOverstyringer(ansattId, brukerIds).map { BrukerId(it.fnr) to it.expires }

}