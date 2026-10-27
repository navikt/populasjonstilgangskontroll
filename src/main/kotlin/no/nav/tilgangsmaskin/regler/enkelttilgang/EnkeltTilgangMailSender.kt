package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailBody
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient.MailAddress
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangMailSender(
    private val proxy: EntraProxyTjeneste,
) {
    private val log = getLogger(javaClass)

    fun sendRapport(rapport: EnkeltTilgangRapport) {
        log.info("Sender enkelttilgang rapport med ${rapport.databaseIds.size} ansatte til ${rapport.leder}")
        runCatching {
            val message =
                MailRequest(
                    MailMessage(
                        "TEST BÆRUM enkelttilgang rapport til - ${rapport.leder}",
                        MailBody("html", rapport.html),
                        listOf(MAILBOX),
                        listOf(ME),
                    ),
                    true,
                )
            proxy.mail(message)
        }.onSuccess {
            log.info("Rapport sendt til {}", rapport.leder)
        }.onFailure {
            log.error("Kunne ikke sende rapport til ${rapport.leder}", it)
        }.getOrThrow()
    }

    companion object {
        private val MAILBOX = MailRecipient(MailAddress("teknologiavdelingen.team-sikkerhetstjenesten@nav.no"))
        private val ME = MailRecipient(MailAddress("jan-olav.eide@nav.no"))
    }
}
