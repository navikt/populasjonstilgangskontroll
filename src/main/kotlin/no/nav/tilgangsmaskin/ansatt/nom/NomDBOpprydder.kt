package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.utils.LederUtvelger.LeaderChangedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.concurrent.TimeUnit.HOURS
import org.slf4j.LoggerFactory.getLogger

@Component
class NomDBOpprydder(registry: MeterRegistry, private val nom: NomTjeneste) : AbstractLederUtvelger() {

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

abstract class AbstractLederUtvelger(var erLeder: Boolean = false) {
    protected val hostname = InetAddress.getLocalHost().hostName
    protected fun doHandleLeaderChange()  = Unit

    private val log = getLogger(javaClass)

    @EventListener(LeaderChangedEvent::class)
    fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
        if (erLeder) {
            log.info("Denne instansen ($hostname) er nå leder")
            doHandleLeaderChange()
        }
        else {
            log.info("Denne instansen ($hostname) er IKKE leder, lederen er ${event.leder}")
        }
    }
}