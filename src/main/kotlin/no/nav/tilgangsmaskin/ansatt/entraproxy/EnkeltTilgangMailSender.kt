package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.samskivert.mustache.Mustache.Compiler
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailBody
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient.MailAddress
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.DAYS
import kotlin.text.Charsets.UTF_8

@Component
class EnkeltTilgangMailSender(
    private val enkelt: EnkeltTilgangTjeneste,
    private val proxy: EntraProxyTjeneste,
    mustacheCompiler: Compiler,
) {
    private val log = getLogger(javaClass)
    private val template =
        ClassPathResource("templates/mail/enkelt-tilgang.mustache")
            .inputStream
            .bufferedReader(UTF_8)
            .use { mustacheCompiler.compile(it) }

    fun sendRapporter() {
        runCatching {
            val data = enkelt.ikkeRapportertePrLeder()
            data.forEach { rapport ->
                val ansatte =
                    rapport.ansatte
                        .filter { it.enhet == "0105" }
                        .sortedWith(compareBy(nullsLast()) { it.created })
                if (ansatte.isEmpty()) return@forEach

                runCatching {
                    val tilganger =
                        ansatte.map { tilgang ->
                            TilgangRad(
                                tilgang.id.verdi,
                                tilgang.created?.let(DATO::format) ?: MANGLER_DATA,
                                tilgang.created?.let(TIDSPUNKT::format) ?: MANGLER_DATA,
                                varighet(tilgang.created, tilgang.varighet),
                                tilgang.gtNavn ?: tilgang.gt ?: MANGLER_DATA,
                                tilgang.begrunnelse,
                                tilgang.system?.substringAfterLast(':') ?: MANGLER_DATA,
                            )
                        }
                    val body = template.execute(mapOf("tilganger" to tilganger, "leder" to rapport.leder))
                    val message =
                        MailRequest(
                            MailMessage(
                                "TEST Enkelttilgang - ${rapport.leder.navident.verdi}",
                                MailBody("html", body),
                                listOf(MailRecipient(MailAddress(RECIPIENT))),
                                emptyList(),
                                emptyList(),
                                emptyList(),
                            ),
                            false,
                        )
                    proxy.mail(message)
                }.onSuccess { log.info("Report email sent for leader {}", rapport.leder.navident.verdi) }
                    .onFailure { log.error("Failed to send report email for leader ${rapport.leder.navident.verdi}", it) }
            }
        }.onFailure { log.error("Failed to load report email data", it) }
    }

    data class TilgangRad(
        val ansatt: String,
        val dato: String,
        val tidspunkt: String,
        val varighet: String,
        val enhet: String,
        val begrunnelse: String,
        val system: String,
    )

    companion object {
        private const val RECIPIENT = "jan-olav.eide@nav.no"
        private const val MANGLER_DATA = ""
        private val TIDSSONE = ZoneId.of("Europe/Oslo")
        private val DATO = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(TIDSSONE)
        private val TIDSPUNKT = DateTimeFormatter.ofPattern("HH:mm").withZone(TIDSSONE)

        private fun varighet(created: Instant?, expires: Instant): String {
            val fra = created?.atZone(TIDSSONE)?.toLocalDate() ?: return MANGLER_DATA
            val til = expires.atZone(TIDSSONE).toLocalDate()
            return if (fra == til) "Ut dagen" else "${DAYS.between(fra, til) + 1} dager"
        }
    }
}
