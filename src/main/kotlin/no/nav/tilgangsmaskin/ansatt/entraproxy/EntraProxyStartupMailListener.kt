package no.nav.tilgangsmaskin.ansatt.entraproxy

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EntraProxyStartupMailListener(
    private val proxy: EntraProxyTjeneste,
) {
    private val log = getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun sendStartupMail() {
        runCatching { proxy.mail("Populasjonstilgangskontroll startet") }
            .onSuccess { log.info("Startup email sent") }
            .onFailure { log.error("Failed to send startup email", it) }
    }

    companion object {
        private const val RECIPIENT = "jan-olav.eide@nav.no"
    }
}
