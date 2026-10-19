package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.samskivert.mustache.Mustache.Compiler
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailBody
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest.MailMessage.MailRecipient.MailAddress
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.INGEN_LEDER
import no.nav.tilgangsmaskin.regler.enkelttilgang.LederEnkeltTilganger
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
            val data = rapporterMedLeder(enkelt.ikkeRapportertePrLeder(setOf(AREMARK)))
            data.forEach { rapport ->
                val ansatte =
                    rapport.ansatte
                        .filter { it.enhet == "0105" }
                        .filterNot { it.begrunnelse.startsWith("Oppgave") }
                        .sortedWith(compareBy(nullsLast()) { it.created })
                if (ansatte.isEmpty()) return@forEach

                runCatching {
                    val tilganger =
                        ansatte.mapIndexed { index, tilgang ->
                            TilgangRad(
                                tilgang.id.verdi,
                                tilgang.created?.let(DATO::format) ?: MANGLER_DATA,
                                tilgang.created?.let(TIDSPUNKT::format) ?: MANGLER_DATA,
                                varighet(tilgang.created, tilgang.varighet),
                                tilgang.gtNavn ?: tilgang.gt ?: MANGLER_DATA,
                                tilgang.begrunnelse,
                                visningsnavnForSystem(tilgang.system),
                                if (index % 2 == 0) "#d3dfd7" else "#c5d4cb",
                            )
                        }
                    val body = template.execute(mapOf("tilganger" to tilganger, "leder" to rapport.leder))
                    val message =
                        MailRequest(
                            MailMessage(
                                "TEST enkelttilgang rapport til - ${rapport.leder}",
                                MailBody("html", body),
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
                }
            }
        }.onFailure { log.warn("Kunne ikke lage rapport", it) }
    }

    data class TilgangRad(
        val ansatt: String,
        val dato: String,
        val tidspunkt: String,
        val varighet: String,
        val enhet: String,
        val begrunnelse: String,
        val system: String,
        val bakgrunnsfarge: String,
    )

    companion object {
        private const val AREMARK = "10108000398"
        private val MAILBOX = MailRecipient(MailAddress("teknologiavdelingen.team-sikkerhetstjenesten@nav.no"))
        private val ME = MailRecipient(MailAddress("jan-olav.eide@nav.no"))
        private const val MANGLER_DATA = ""
        private val TIDSSONE = ZoneId.of("Europe/Oslo")
        private val DATO = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(TIDSSONE)
        private val TIDSPUNKT = DateTimeFormatter.ofPattern("HH:mm").withZone(TIDSSONE)

        internal fun varighet(
            created: Instant?,
            expires: Instant,
        ): String {
            val fra = created?.atZone(TIDSSONE)?.toLocalDate() ?: return MANGLER_DATA
            val til = expires.atZone(TIDSSONE).toLocalDate()
            return when (val dager = DAYS.between(fra, til)) {
                0L -> "Ut dagen"
                1L -> "1 dag"
                else -> "$dager dager"
            }
        }
    }
}

internal fun visningsnavnForSystem(system: String?): String = system?.let { SYSTEM_VISNINGSNAVN[it] ?: it.substringAfterLast(':') } ?: ""

internal fun rapporterMedLeder(rapporter: List<LederEnkeltTilganger>): List<LederEnkeltTilganger> =
    rapporter.filterNot { it.leder == INGEN_LEDER }

private val SYSTEM_VISNINGSNAVN =
    mapOf(
        "prod-fss:isa:gosys" to "Gosys personoppslag",
        "prod-fss:histark:histark-core-api" to "Histark",
        "prod-gcp:oppgavehandtering:gosys-oppgavebehandling-api" to "Gosys oppgaveliste",
    )
