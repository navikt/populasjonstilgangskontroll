package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.HOURS
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.slf4j.LoggerFactory.getLogger

@Component
class NomDBOpprydder(registry: MeterRegistry, private val nom: NomTjeneste) : LeaderAware() {

    private val log = getLogger(javaClass)

    private val counter = Counter.builder("vaktmester.rader.fjernet")
        .description("Antall rader fjernet")
        .register(registry)

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
    fun ryddOpp(): Int {
        if (!erLeder) {
            log.info("Vaktmester er ikke leder, hopper over rydding i Nom-databasen")
            return 0
        }
        log.info("Vaktmester rydder opp i Nom-databasen")
        val antall = nom.ryddOpp()
        counter.increment(antall.toDouble())
        return antall
    }
}

