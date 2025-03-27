package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import io.micrometer.core.annotation.Counted
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.TimeExtensions.intervallSiden
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Counted
class SøskenAksessTeller(private val accessor: TokenClaimsAccessor) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun registrerAksess(ansattId: AnsattId, brukerId: BrukerId) =
        true.also {
            log.warn("$ansattId slo opp søsken $brukerId fra system ${accessor.systemNavn}")
        }
}

@Component
class AvdødAksessTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun registrerAksess(ansattId: AnsattId, brukerId: BrukerId, dødsdato: LocalDate) =
        true.also {
            // TODO Endre til false når vi faktisk skal håndtere døde
            val intervall = dødsdato.intervallSiden()
            Counter.builder("dead.attempted.total")
                .description("Number of deceased users attempted accessed")
                .tag("months",intervall)
                .tag("system",accessor.system ?: "N/A")
                .register(meterRegistry).increment().also {
                    log.warn("$ansattId slo opp avdød bruker $brukerId med dødsdato $intervall måneder siden fra system ${accessor.systemNavn}")
                }
        }
}