package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.LederUtvelger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.HOURS

@Component
class NomVaktmester(
    private val nom: NomOperasjoner,
    private val utvelger: LederUtvelger,
    private val registry: MeterRegistry
) {

    private val log = getLogger(javaClass)
    private val counter = Counter.builder("vaktmester.rader.fjernet")
        .description("Antall rader fjernet")
        .register(registry)

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
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

