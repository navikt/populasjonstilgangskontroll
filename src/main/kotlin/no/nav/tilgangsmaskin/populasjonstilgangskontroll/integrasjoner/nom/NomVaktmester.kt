package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LeaderElector
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.MINUTES

@Component
class NomVaktmester(private val nom: NomTjeneste, private val elector: LeaderElector) {

    private val log = getLogger(NomVaktmester::class.java)

    @Scheduled(fixedRate = 60, timeUnit = MINUTES)
    fun ryddOpp() {
        if ((elector.erLeder)) {
            log.info("Vaktmester fjerner utgått informasjon")
            nom.ryddOpp().also {
                if (it > 0) {
                    log.info("Vaktmester fjernet $it rad(er) med utgått informasjon")
                }
            }
        }
        else {
            log.trace("Vaktmester er ikke leder")
        }
    }
}

