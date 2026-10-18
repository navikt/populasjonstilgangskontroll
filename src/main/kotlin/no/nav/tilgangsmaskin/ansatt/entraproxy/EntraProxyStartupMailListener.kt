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
                message =
                    MailMessage(
                        subject = "Populasjonstilgangskontroll startet",
                        body = MailBody(contentType = "Text", content = "Populasjonstilgangskontroll startet"),
                        toRecipients = listOf(MailRecipient(MailAddress(RECIPIENT))),
                        ccRecipients = emptyList(),
                        bccRecipients = emptyList(),
                        replyTo = emptyList(),
                    ),
                saveToSentItems = false,
            )
    }
}
