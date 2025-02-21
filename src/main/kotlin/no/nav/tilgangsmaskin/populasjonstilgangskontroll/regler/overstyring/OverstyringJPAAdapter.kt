package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class OverstyringJPAAdapter(private val repository: OverstyringRepository)  {

    fun lagre(ansattId: String, brukerId: String, begrunnelse: String, varighet: Duration) =
        repository.save(OverstyringEntity(ansattId, brukerId,begrunnelse,Instant.now().plus(varighet.toJavaDuration())))

    fun nyesteOverstyring(ansattId: String, brukerId: String) = repository.findByNavidAndFnrOrderByCreatedDesc(ansattId, brukerId)?.firstOrNull()
}