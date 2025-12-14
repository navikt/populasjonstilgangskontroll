package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.Counter.builder
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.*

@Component
class NomDBOpprydder(registry: MeterRegistry, private val nom: NomTjeneste) : LeaderAware() {

    private val log = getLogger(javaClass)

    private val antallKall = builder("vaktmester.kall")
        .description("Antall ganger kalt")
        .register(registry)
    private val counter = builder("vaktmester.rader.fjernet")
        .description("Antall rader fjernet")
        .register(registry)

    @PostConstruct
    @Scheduled(fixedRate = 10, timeUnit = MINUTES)
    fun ryddOpp(): Int {
        if (!erLeder) {
            log.info("Vaktmester er ikke leder, hopper over rydding i Nom-databasen")
            return 0
        }
        log.info("Vaktmester rydder opp i Nom-databasen")
        val antall = nom.ryddOpp()
        antallKall.increment()
        counter.increment(antall.toDouble())
        return antall
    }
}

