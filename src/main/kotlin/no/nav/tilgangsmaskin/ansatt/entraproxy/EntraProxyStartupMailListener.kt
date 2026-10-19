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
                            <p>Hei,</p>
                            <p>«Enkelttilgang» gir medarbeidere mulighet til å gi seg selv midlertidig tilgang til enkeltbrukere ved behov, mot å begrunne oppslaget. Begrunnelsen skal tydelig forklare hvorfor man trenger tilgang til brukeren, og skal ikke inneholde sensitive opplysninger. Vi ber deg som leder følge opp eventuelle mangelfulle begrunnelser med aktuelle medarbeidere.</p>
                            <p>Disse enkelttilgangene er gitt til dine medarbeidere den siste måneden:</p>
                            <table>
                              <thead>
                                <tr>
                                  <th>Ansatt</th>
                                  <th>Dato</th>
                                  <th>Tidspunkt</th>
                                  <th>Varighet</th>
                                  <th>Brukers fødselsdato</th>
                                  <th>Brukers enhet</th>
                                  <th>Oppgitt begrunnelse</th>
                                  <th>Utført fra system</th>
                                </tr>
                              </thead>
                              <tbody>
                                <tr><td>S123456</td><td>02.10.26</td><td>08:31</td><td>Ut dagen</td><td>02.08</td><td>Orkland</td><td>Tjenestlig behov</td><td>Gosys personoppslag</td></tr>
                                <tr><td>F241472</td><td>02.10.26</td><td>08:29</td><td>Ut dagen</td><td>15.01</td><td>Midtbyen</td><td>Oppgave i Arena</td><td>Gosys personoppslag</td></tr>
                                <tr><td>E031628</td><td>02.10.26</td><td>07:51</td><td>29 dager</td><td>02.11</td><td>Arendal</td><td>trenger til behandling</td><td>Gosys oppgaveliste</td></tr>
                              </tbody>
                            </table>
                            <p>Les mer om enkelttilgang på navet.</p>
                            <p>Dersom du har spørsmål om konkrete tilganger gitt til dine ansatte så meld fra til teknologiavdelingen.team-sikkerhetstjenesten@nav.no</p>
                            <p>Generelle spørsmål og tilbakemeldinger om løsningen kan stilles på Viva Engage (Pilot enkelttilgang).</p>
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
