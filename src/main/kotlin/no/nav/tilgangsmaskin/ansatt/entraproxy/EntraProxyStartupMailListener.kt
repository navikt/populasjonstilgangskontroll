package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.samskivert.mustache.Mustache
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
    mustacheCompiler: Mustache.Compiler,
) {
    private val log = getLogger(javaClass)
    private val message =
        MailRequest(
            MailMessage(
                "Populasjonstilgangskontroll startet",
                MailBody("html", mustacheCompiler.compile("mail/enkelt-tilgang").execute(emptyMap<String, Any>())),
                listOf(MailRecipient(MailAddress(RECIPIENT))),
                emptyList(),
                emptyList(),
                emptyList(),
            ),
            false,
        )

    @EventListener(ApplicationReadyEvent::class)
    fun sendStartupMail() {
        runCatching { proxy.mail(message) }
            .onSuccess { log.info("Startup email sent") }
            .onFailure { log.error("Failed to send startup email", it) }
    }

    companion object {
        private const val RECIPIENT = "jan-olav.eide@nav.no"
    }
}
