package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.*

@Component
class NomDBOpprydder(
    private val nom: NomTjeneste,
    private val antallKall: NomKallTeller,
    private val raderFjernet: NomRaderFjernetTeller) : LeaderAware() {

    private val log = getLogger(javaClass)

    override fun doHandleLeaderChange() {
        ryddOpp()
    }

    @Scheduled(fixedRate = 24, timeUnit = HOURS)
    fun ryddOpp() =
        somLeder("daglig opprydding i Nom-databasen", {
            log.info("Vaktmester rydder opp i Nom-databasen")
            val antall = nom.ryddOpp()
            antallKall.tell()
            raderFjernet.tell(antall)
            antall
        }) { 0 }
}

