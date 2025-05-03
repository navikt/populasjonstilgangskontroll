package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component


@Component
class HabilitetTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "habilitet.oppslag.total", "Forsøk på å slå opp nærstående")

@Component
class AvdødTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "dead.oppslag.total", "Forsøk på å slå opp avdøde")
