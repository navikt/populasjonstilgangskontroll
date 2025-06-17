package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import java.util.concurrent.TimeUnit.HOURS
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NomVaktmester(private val nom: Nom, private val utvelger: LederUtvelger, registry: MeterRegistry) {

    init {
        ryddOpp()
    }

    private val log = getLogger(javaClass)
    private val counter = Counter.builder("vaktmester.rader.fjernet")
        .description("Antall rader fjernet")
        .register(registry)

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
    fun ryddOpp(): Int {
        if (!utvelger.erLeder) return 0
        log.info("Vaktmester rydder opp i Nom-databasen")
        val antall = nom.ryddOpp()
        if (antall > 0) {
            counter.increment(antall.toDouble())
            log.info("Vaktmester ryddet opp $antall rad(er) med utgått informasjon om ansatte som ikke lenger jobber i Nav")
        }
        return antall
    }
}



