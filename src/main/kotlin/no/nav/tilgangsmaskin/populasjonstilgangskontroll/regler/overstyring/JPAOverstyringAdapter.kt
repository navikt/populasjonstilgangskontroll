package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class JPAOverstyringAdapter(private val repository: OverstyringRepository)  {

    fun lagre(ansattId: String, brukerId: String, varighet: Duration) =
        repository.save(Overstyring().apply {
            navid = ansattId
            fnr = brukerId
            expires =  Instant.now().plus(varighet.toJavaDuration())
        })

    fun nyesteOverstyring(navid: String, fnr: String) = repository.findByNavidAndFnrOrderByCreatedDesc(navid, fnr)?.firstOrNull()
}