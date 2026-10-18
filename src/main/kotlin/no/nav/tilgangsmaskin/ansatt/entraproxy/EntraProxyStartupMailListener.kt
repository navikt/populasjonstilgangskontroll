package no.nav.tilgangsmaskin.ansatt.entraproxy

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory.getLogger

@Component
class EntraProxyStartupMailListener(private val entraProxyTjeneste: EntraProxyTjeneste) {
    private val log = getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun sendStartupMail() {
        val message = SimpleMailMessage().apply {
            setTo(RECIPIENT)
            subject = "Populasjonstilgangskontroll startet"
            text = "Populasjonstilgangskontroll startet."
        }

        runCatching { entraProxyTjeneste.mail(message) }
            .onSuccess { log.info("Startup email sent") }
            .onFailure { log.error("Failed to send startup email", it) }
    }

    companion object {
        private const val RECIPIENT = "jan-olav.eide@nav.no"
    }
}
