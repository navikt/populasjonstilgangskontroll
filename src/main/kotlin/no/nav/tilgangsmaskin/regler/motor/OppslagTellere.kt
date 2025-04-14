package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SøskenOppslagTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
    fun registrerOppslag(ansattId: AnsattId, brukerId: BrukerId) =
        true.also {
            // TODO Endre til false når vi faktisk skal nekte
            Counter.builder("siblings.attempted.total")
                .description("Number of siblings  attempted accessed")
                .tag("system", accessor.system ?: "N/A")
                .register(meterRegistry).increment()
        }
}


@Component
class AvdødOppslagTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
    fun registrerOppslag(ansattId: AnsattId, brukerId: BrukerId, dødsdato: LocalDate) =
        true.also {
            // TODO Endre til false når vi faktisk skal nekte
            val intervall = dødsdato.intervallSiden()
            Counter.builder("dead.attempted.total")
                .description("Number of deceased users attempted accessed")
                .tag("months", intervall)
                .tag("system", accessor.system ?: "N/A")
                .register(meterRegistry).increment()
        }

    @Component
    class PartnerOppslagTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
        fun registrerOppslag(ansattId: AnsattId, brukerId: BrukerId, relasjon: FamilieRelasjon) =
            true.also {
                // TODO Endre til false når vi faktisk skal nekte
                Counter.builder("partners.attempted.total")
                    .description("Number of partners attempted accessed")
                    .tag("system", accessor.system ?: "N/A")
                    .register(meterRegistry).increment()
            }
    }
}