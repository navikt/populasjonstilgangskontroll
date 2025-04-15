package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.stereotype.Component

abstract class HabilitetsTeller(
    protected val registry: MeterRegistry,
    protected val accessor: TokenClaimsAccessor,
    private val tellerNavn: String,
    private val beskrivelse: String
) {

    fun registrerOppslag(ok: Boolean = false, vararg tags: Pair<String, String> = emptyArray()) =
        ok.also {
            Counter.builder(tellerNavn)
                .description(beskrivelse)
                .tag("system", accessor.system ?: "N/A")
                .apply { tags.forEach { tag(it.first, it.second) } }
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
class AvdødTeller(registry: MeterRegistry, accessor: TokenClaimsAccessor) :
    HabilitetsTeller(registry, accessor, "dead.attempted.total", "Forsøk på å slå opp avdøde")

@Component
class PartnerOppslagTeller(registry: MeterRegistry, accessor: TokenClaimsAccessor) :
    HabilitetsTeller(registry, accessor, "partners.attempted.total", "Forsøk på å slå opp partner(e)")
