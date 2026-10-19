package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.felles.leder.LeaderAware
import no.nav.tilgangsmaskin.ansatt.entraproxy.EnkeltTilgangMailSender
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnGCP
class EnkeltTilgangRapportør(
    private val mailSender: EnkeltTilgangMailSender,
) : LeaderAware() {
    @Scheduled(initialDelayString = "2m")
    fun sendRapporterVedOppstart() {
        // mailSender.sendRapporter()
    }

    @Scheduled(cron = "0 */10 * * * *", zone = "Europe/Oslo")
    fun sendRapporter() =
        somLeder {
            mailSender.sendRapporter()
        }
}
