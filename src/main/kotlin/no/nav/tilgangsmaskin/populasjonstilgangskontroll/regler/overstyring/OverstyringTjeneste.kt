package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Component
class OverstyringTjeneste(private val adapter: JPAOverstyringAdapter) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    fun nyesteOverstyring(id: NavId, fødselsnummer: Fødselsnummer) =
        adapter.nyesteOverstyring(id.verdi, fødselsnummer.verdi)

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer, varighet: Duration = 5.minutes) =
         adapter.lagre(ansattId.verdi, brukerId.verdi, varighet)
}

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