package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.sikkerhetstjenesten.entraproxy.felles.leder.LeaderAware
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.*

@Component
class NomDBOpprydder(
    private val nom: NomTjeneste) : LeaderAware() {

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
    fun ryddOpp() =
        somLeder("daglig opprydding i Nom-databasen", {
            nom.ryddOpp()
        })
}

