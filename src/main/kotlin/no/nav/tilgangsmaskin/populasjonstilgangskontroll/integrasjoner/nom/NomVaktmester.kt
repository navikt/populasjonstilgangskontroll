package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LederUtvelger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.SECONDS

@Component
class NomVaktmester(private val nom: NomTjeneste, private val lederUtvelger: LederUtvelger) {

    private val log = getLogger(NomVaktmester::class.java)

    @Scheduled(fixedRate = 60, timeUnit = SECONDS)
    fun ryddOpp() {
        if (lederUtvelger.erLeder) {
            log.info("Jeg er vaktmester og rydder opp")
            nom.ryddOpp().also {
                if (it > 0) {
                    log.info("Vaktmester ryddet opp $it rad(er) med utgått informasjon")
                }
                else {
                    log.info("Vaktmester fant ingen utgått informasjon å rydde opp i")
                }
            }
        } else {
            log.info("Jeg er ikke vaktmester og kan ikke rydde opp")
        }
    }
}

