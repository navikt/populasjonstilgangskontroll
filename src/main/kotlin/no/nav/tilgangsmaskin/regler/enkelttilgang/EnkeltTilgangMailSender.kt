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
                        "Enkelttilgang rapport",
                        MailBody("html", rapport.html),
                        listOf(MOTTAGER)),  // TODO endre ved prodsetting
                         //listOf(rapport.leder.epost),
                    true)
            proxy.mail(message)
        }.onSuccess {
            log.info("Rapport sendt til {}", rapport.leder.epost)
        }.onFailure {
            log.error("Kunne ikke sende rapport til ${rapport.leder.epost}", it)
        }.getOrThrow()
    }

    companion object {
        private const val POSTBOKS = "teknologiavdelingen.team-sikkerhetstjenesten@nav.no"
        private val MOTTAGER = MailRecipient(MailAddress(POSTBOKS))
    }
}
