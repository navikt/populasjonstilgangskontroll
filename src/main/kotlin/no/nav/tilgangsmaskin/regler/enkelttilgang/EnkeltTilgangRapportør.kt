package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.felles.leder.LeaderAware
import no.nav.tilgangsmaskin.ansatt.entraproxy.EnkeltTilgangMailSender
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnGCP
class EnkeltTilgangRapportør(
    private val mailer: EnkeltTilgangMailSender,
) : LeaderAware() {
    @Scheduled(cron = "0 15 13 * * TUE", zone = "Europe/Oslo")
    fun sendRapporter() =
        somLeder {
            mailer.sendRapporter()
        }
}
