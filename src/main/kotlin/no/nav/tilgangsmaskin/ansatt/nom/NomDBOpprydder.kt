package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.*

@Component
class NomDBOpprydder(
    private val nom: NomTjeneste) : LeaderAware() {


    override fun doHandleLeaderChange() {
        ryddOpp()
    }

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
    fun ryddOpp() =
        somLeder("daglig opprydding i Nom-databasen", {
            nom.ryddOpp()
        }) { 0 }
}

