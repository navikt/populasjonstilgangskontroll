package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import org.springframework.stereotype.Component
import java.time.ZoneId.systemDefault
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Component
class OverstyringJPAAdapter(private val repository: OverstyringRepository)  {

    fun overstyr(ansattId: String, data: OverstyringData) =
        repository.save(OverstyringEntity(ansattId, data.brukerId.verdi,data.begrunnelse,data.gyldigtil.atStartOfDay(systemDefault()).toInstant().plus(1.days.toJavaDuration())))

    fun gjeldendeOverstyringGyldighetDato(ansattId: String, brukerId: String, brukerIds: List<String>) = repository.finnGjeldendeOverstyring(ansattId, brukerId, brukerIds)?.expires
}