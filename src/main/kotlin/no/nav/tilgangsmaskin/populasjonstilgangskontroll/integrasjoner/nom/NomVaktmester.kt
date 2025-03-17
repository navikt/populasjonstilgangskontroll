package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.SECONDS


@Component
class NomVaktmester(private val nom: NomTjeneste) {

    private val log = getLogger(NomVaktmester::class.java)

    @Scheduled(fixedRate = 60, timeUnit = SECONDS)
    fun fjern() {
        log.info("Vaktmester rydder opp")
        nom.ryddOpp()
    }
}