package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import org.springframework.stereotype.Component
import java.time.ZoneId.systemDefault
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Component
class OverstyringJPAAdapter(private val repository: OverstyringRepository)  {

    fun overstyr(ansattId: String, brukerId: String, metadata: OverstyringMetadata) =
        repository.save(OverstyringEntity(ansattId, brukerId,metadata.begrunnelse,metadata.varighet.atStartOfDay(systemDefault()).toInstant().plus(1.days.toJavaDuration())))

    fun gjeldendeOverstyringGyldighetDato(ansattId: String, brukerId: String) = repository.finnGjeldendeOverstyring(ansattId, brukerId)?.expires
}