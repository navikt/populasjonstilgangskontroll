package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SøskenOppslagTeller(private val accessor: TokenClaimsAccessor) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun registrerOppslag(ansattId: AnsattId, brukerId: BrukerId) =
        true.also {
            log.warn("$ansattId har manglende habilitet for oppslag mot $brukerId fra system ${accessor.systemNavn}")
        }
}

@Component
class AvdødOppslagTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun registrerOppslag(ansattId: AnsattId, brukerId: BrukerId, dødsdato: LocalDate) =
        true.also {
            // TODO Endre til false når vi faktisk skal nekte
            val intervall = dødsdato.intervallSiden()
            Counter.builder("dead.attempted.total")
                .description("Number of deceased users attempted accessed")
                .tag("months", intervall)
                .tag("system", accessor.system ?: "N/A")
                .register(meterRegistry).increment().also {
                    log.warn("$ansattId slo opp avdød bruker $brukerId med dødsdato $intervall måneder siden fra system ${accessor.systemNavn}")
                }
        }

    @Component
    class PartnerOppslagTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
        private val log = LoggerFactory.getLogger(javaClass)
        fun registrerOppslag(ansattId: AnsattId, brukerId: BrukerId, relasjon: FamilieRelasjon) =
            true.also {
                // TODO Endre til false når vi faktisk skal nekte
                Counter.builder("partner.attempted.total")
                    .description("Number of partners attempted accessed")
                    .tag("system", accessor.system ?: "N/A")
                    .register(meterRegistry).increment()
            }
    }
}