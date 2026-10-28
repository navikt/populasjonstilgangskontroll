package no.nav.tilgangsmaskin.regler.enkelttilgang

import com.samskivert.mustache.Mustache.Compiler
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.DAYS
import kotlin.text.Charsets.UTF_8

@Component
class EnkeltTilgangRapportGenerator(mustacheCompiler: Compiler, private val enkelt: EnkeltTilgangTjeneste) {
    private val template =
        ClassPathResource(TEMPLATE)
            .inputStream
            .bufferedReader(UTF_8)
            .use { mustacheCompiler.compile(it) }

    fun lagRapporter() = enkelt.ikkeRapportertePrLeder(setOf(AREMARK)).mapNotNullTo(mutableSetOf(), ::lagRapporter)

    internal fun lagRapporter(rapport: LederEnkeltTilganger): EnkeltTilgangRapport? {
        if (rapport.leder == INGEN_LEDER) return null
        val ansatte =
            rapport.ansatte
                .filter { it.enhet == BÆRUM }
                .filterNot { it.begrunnelse.startsWith("Oppgave (") }
                .sortedWith(compareBy(nullsLast()) { it.created })
        if (ansatte.isEmpty()) return null

        val tilganger =
            ansatte.mapIndexed { index, tilgang ->
                with(tilgang) {
                    TilgangRad(
                        id.verdi,
                        created?.let(DATO::format) ?: MANGLER_DATA,
                        created?.let(TIDSPUNKT::format) ?: MANGLER_DATA,
                        varighet(created, varighet),
                        gtNavn ?: gt ?: MANGLER_DATA,
                        begrunnelse,
                        visningsnavnForSystem(system),
                        if (index % 2 == 0) "#d3dfd7" else "#c5d4cb",
                    )
                }
            }
        return EnkeltTilgangRapport(
            template.execute(mapOf("tilganger" to tilganger, "leder" to rapport.leder)),
            ansatte.mapTo(mutableSetOf()) { it.databaseId },
            rapport.leder,
        )
    }

    private data class TilgangRad(
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
        private const val BÆRUM = "0219"
        private const val TEMPLATE = "templates/mail/enkelt-tilgang.mustache"
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

private val SYSTEM_VISNINGSNAVN =
    mapOf(
        "prod-fss:isa:gosys" to "Gosys personoppslag",
        "prod-fss:histark:histark-core-api" to "Histark",
        "prod-gcp:oppgavehandtering:gosys-oppgavebehandling-api" to "Gosys oppgaveliste",
    )
