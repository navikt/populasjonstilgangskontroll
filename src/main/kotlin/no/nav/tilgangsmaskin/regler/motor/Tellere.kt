package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component


@Component
class SøskenOppslagTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "siblings.attempted.total", "Forsøk på å slå opp søsken")

@Component
class EgneDataOppslagTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "own.attempted.total", "Forsøk på å slå opp egne data")

@Component
class ForeldreBarnOppslagTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "parentsorchildren.attempted.total", "Forsøk på å slå opp foreldre eller barn")

@Component
class AvdødTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "dead.attempted.total", "Forsøk på å slå opp avdøde")

@Component
class PartnerOppslagTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "partners.attempted.total", "Forsøk på å slå opp partner(e)")
