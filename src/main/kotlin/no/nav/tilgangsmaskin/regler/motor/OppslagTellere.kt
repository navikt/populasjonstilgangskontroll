package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.stereotype.Component
import java.time.LocalDate

abstract class HabilitetsTeller(
    protected val registry: MeterRegistry,
    protected val accessor: TokenClaimsAccessor,
    private val tellerNavn: String,
    private val beskrivelse: String
) {

    fun registrerOppslag(ok: Boolean = false) =
        ok.also {
            Counter.builder(tellerNavn)
                .description(beskrivelse)
                .tag("system", accessor.system ?: "N/A")
                .register(registry).increment()
        }

}

@Component
class SøskenOppslagTeller(registry: MeterRegistry, accessor: TokenClaimsAccessor) :
    HabilitetsTeller(registry, accessor, "siblings.attempted.total", "Forsøk på å slå opp søsken")

@Component
class EgneDataTeller(registry: MeterRegistry, accessor: TokenClaimsAccessor) :
    HabilitetsTeller(registry, accessor, "own.attempted.total", "Forsøk på å slå opp egne data")

@Component
class ForeldreBarnTeller(registry: MeterRegistry, accessor: TokenClaimsAccessor) :
    HabilitetsTeller(registry, accessor, "parentsorchildren.attempted.total", "Forsøk på å slå opp foreldre eller barn")

@Component
class AvdødOppslagTeller(private val registry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
    fun registrerOppslag(dødsdato: LocalDate) {
        val intervall = dødsdato.intervallSiden()
        Counter.builder("dead.attempted.total")
            .description("Number of deceased users attempted accessed")
            .tag("months", intervall)
            .tag("system", accessor.system ?: "N/A")
            .register(registry).increment()
    }
}

@Component
class PartnerOppslagTeller(registry: MeterRegistry, accessor: TokenClaimsAccessor) :
    HabilitetsTeller(registry, accessor, "partners.attempted.total", "Forsøk på å slå opp partner(e)")
