package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class OverstyringJPAAdapter(private val repository: OverstyringRepository)  {

    fun lagre(ansattId: String, brukerId: String, metadata: OverstyringMetadata) =
        repository.save(OverstyringEntity(ansattId, brukerId,metadata.begrunnelse,metadata.varighet.atStartOfDay(ZoneId.systemDefault()).toInstant()))

    fun nyesteOverstyring(ansattId: String, brukerId: String) = repository.findByNavidAndFnrOrderByCreatedDesc(ansattId, brukerId)?.firstOrNull()
}