package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LederUtvelger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.SECONDS

@Component
class NomVaktmester(private val nom: NomOperasjoner, private val utvelger: LederUtvelger, private val registry: MeterRegistry) {

    private val log = getLogger(NomVaktmester::class.java)
    private val counter =  Counter.builder("vaktmester.rader.fjernet")
    .description("Antall rader fjernet")
    .register(registry)

    @Scheduled(fixedRate = 60, timeUnit = SECONDS)
    fun ryddOpp() =
        if (utvelger.erLeder) {
            nom.ryddOpp().also {
                if (it > 0) {
                    counter.increment(it.toDouble())
                    log.info("Vaktmester ryddet opp $it rad(er) med utgått informasjon om ansatte som ikke lenger jobber i Nav")
                }
            }
        } else 0
}

