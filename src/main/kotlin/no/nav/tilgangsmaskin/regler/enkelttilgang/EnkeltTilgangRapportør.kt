package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.felles.leder.LeaderAware
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnGCP
class EnkeltTilgangRapportør(
    private val mailer: EnkeltTilgangMailSender,
    private val enkelt: EnkeltTilgangTjeneste,
    private val generator: EnkeltTilgangRapportGenerator,
) : LeaderAware() {
    private val log = getLogger(javaClass)

    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Oslo")
    fun sendRapporter() =
        somLeder {
            runCatching {
                val rapporter = generator.lagRapporter()
                rapporter.forEach { rapport ->
                    mailer.sendRapport(rapport)
                    enkelt.markerRapportert(rapport)
                }
            }.onFailure { log.warn("Kunne ikke håndtere rapport", it) }
        }
}
