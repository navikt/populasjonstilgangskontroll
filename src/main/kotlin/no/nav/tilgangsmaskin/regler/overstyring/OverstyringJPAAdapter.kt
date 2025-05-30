package no.nav.tilgangsmaskin.regler.overstyring

import java.time.ZoneId.systemDefault
import org.springframework.stereotype.Component

@Component
class OverstyringJPAAdapter(private val repository: OverstyringRepository) {

    fun overstyr(ansattId: String, data: OverstyringData) =
        with(data) {
            repository.save(
                    OverstyringEntity(
                            ansattId,
                            brukerId.verdi,
                            begrunnelse,
                            gyldigtil.atStartOfDay(systemDefault()).toInstant()))
            Unit
        }

    fun gjeldendeOverstyring(ansattId: String, brukerId: String, brukerIds: List<String>) =
        repository.gjeldendeOverstyring(ansattId, brukerId, brukerIds)?.expires
}