package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailBody
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient.MailAddress
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
        runCatching { proxy.mail(message) }
            .onSuccess { log.info("Startup email sent") }
            .onFailure { log.error("Failed to send startup email", it) }
    }

    companion object {
        private const val RECIPIENT = "jan-olav.eide@nav.no"

        private val message =
            MailRequest(
                MailMessage(
                    "Populasjonstilgangskontroll startet",
                    MailBody(
                        "html",
                        """
                        <html>
                          <body>
                            <table>
                              <tr><th>Tjeneste</th><th>Status</th></tr>
                              <tr><td>Populasjonstilgangskontroll</td><td>Startet</td></tr>
                            </table>
                          </body>
                        </html>
                        """.trimIndent(),
                    ),
                    listOf(MailRecipient(MailAddress(RECIPIENT))),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                ),
                false,
            )
    }
}
