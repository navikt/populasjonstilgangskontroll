package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LederUtvelger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.SECONDS

@Component
class NomVaktmester(private val nom: NomOperasjoner, private val lederUtvelger: LederUtvelger) {

    private val log = getLogger(NomVaktmester::class.java)

    @Scheduled(fixedRate = 60, timeUnit = SECONDS)
    fun ryddOpp() {
        if (lederUtvelger.erLeder) {
            nom.ryddOpp().also {
                if (it > 0) {
                    log.info("Vaktmester ryddet opp $it rad(er) med utgått informasjon om ansatte som ikke lenger jobber i Nav")
                }
            }
        }
    }
}

