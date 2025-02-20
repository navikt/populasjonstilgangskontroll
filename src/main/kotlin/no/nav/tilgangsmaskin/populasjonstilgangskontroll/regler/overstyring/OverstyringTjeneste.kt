package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Component
class OverstyringTjeneste(private val ansatt: AnsattTjeneste, private val bruker: BrukerTjeneste,private val adapter: JPAOverstyringAdapter, private val motor: RegelMotor) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    fun erOverstyrt(id: NavId, fødselsnummer: Fødselsnummer) =
       nyesteOverstyring(id, fødselsnummer) != null

    fun nyesteOverstyring(id: NavId, fødselsnummer: Fødselsnummer) =
        adapter.nyesteOverstyring(id.verdi, fødselsnummer.verdi)

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer, varighet: Duration = 5.minutes) =
         runCatching {
                motor.alleRegler(ansatt.ansatt(ansattId), bruker.bruker(brukerId))
                adapter.lagre(ansattId.verdi, brukerId.verdi, varighet)
            }.getOrElse {
                when (it) {
                    is RegelException -> {
                        if (it.regel.erOverstyrbar) {
                            log.info("${it.regel.beskrivelse.kortNavn} er overstyrbar, gir tilgang til ansatt '${ansattId.verdi}' og bruker '${brukerId.verdi}'")
                            adapter.lagre(ansattId.verdi, brukerId.verdi, varighet)
                        }
                        else {
                            throw it.also {
                                log.error("${it.regel.beskrivelse.kortNavn} er ikke overstyrbar, kunne ikke overstyre tilgang for ansatt '${ansattId.verdi}' og bruker '${brukerId.verdi}'", it)
                            }
                        }
                    }
                    else -> throw it
                }
         }
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