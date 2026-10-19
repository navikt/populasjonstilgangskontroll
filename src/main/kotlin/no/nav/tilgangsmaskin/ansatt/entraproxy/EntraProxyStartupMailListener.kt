package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.samskivert.mustache.Mustache.Compiler
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailBody
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient.MailAddress
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class EntraProxyStartupMailListener(
    private val proxy: EntraProxyTjeneste,
    mustacheCompiler: Compiler,
) {
    private val log = getLogger(javaClass)
    private val template =
        ClassPathResource("templates/mail/enkelt-tilgang.mustache").inputStream
            .bufferedReader(Charsets.UTF_8)
            .use { mustacheCompiler.compile(it) }

    @EventListener(ApplicationReadyEvent::class)
    fun sendStartupMail() {
        runCatching {
            val tilganger =
                listOf(
                    TilgangRad("S123456", "02.10.26", "08:31", "Ut dagen", "02.08", "Orkland", "Tjenestlig behov", "Gosys personoppslag"),
                    TilgangRad("F241472", "02.10.26", "08:29", "Ut dagen", "15.01", "Midtbyen", "Oppgave i Arena", "Gosys personoppslag"),
                    TilgangRad("E031628", "02.10.26", "07:51", "29 dager", "02.11", "Arendal", "trenger til behandling", "Gosys oppgaveliste"),
                )
            val body = template.execute(mapOf("tilganger" to tilganger))
            val message =
                MailRequest(
                    MailMessage(
                        "Populasjonstilgangskontroll startet",
                        MailBody("html", body),
                        listOf(MailRecipient(MailAddress(RECIPIENT))),
                        emptyList(),
                        emptyList(),
                        emptyList(),
                    ),
                    false,
                )
            proxy.mail(message)
        }
            .onSuccess { log.info("Startup email sent") }
            .onFailure { log.error("Failed to send startup email", it) }
    }

    data class TilgangRad(
        val ansatt: String,
        val dato: String,
        val tidspunkt: String,
        val varighet: String,
        val fodselsdato: String,
        val enhet: String,
        val begrunnelse: String,
        val system: String,
    )

    companion object {
        private const val RECIPIENT = "jan-olav.eide@nav.no"
    }
}
