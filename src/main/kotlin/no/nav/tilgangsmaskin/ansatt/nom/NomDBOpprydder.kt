package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.concurrent.TimeUnit.HOURS

@Component
class NomDBOpprydder(private val registry: MeterRegistry, private val nom: Nom)  {

    private val log = LoggerFactory.getLogger(javaClass)

    private val counter = Counter.builder("vaktmester.rader.fjernet")
        .description("Antall rader fjernet")
        .register(registry)

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder: Boolean = false

    @EventListener(LederUtvelgerHandler.LeaderChangedEvent::class)
    fun onApplicationEvent(event: LederUtvelgerHandler.LeaderChangedEvent) {
        erLeder = event.leder == hostname
        log.info("Vaktmester erLeder=$erLeder, me=$hostname, leder=${event.leder}")
        if (erLeder) {
            ryddOpp()
        }
    }

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
    fun ryddOpp(): Int {
        if (!erLeder) {
            log.info("Vaktmester er ikke leder, hopper over rydding i Nom-databasen")
            return 0
        }
        log.info("Vaktmester rydder opp i Nom-databasen")
        val antall = nom.ryddOpp()
        if (antall > 0) {
            counter.increment(antall.toDouble())
            log.info("Vaktmester ryddet opp $antall rad(er) med utgått informasjon om ansatte som ikke lenger jobber i Nav")
        }
        else {
            log.info("Vaktmester fant ingen rader å rydde opp i")
        }
        return antall
    }
}