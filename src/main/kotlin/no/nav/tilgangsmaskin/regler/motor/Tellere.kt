package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.felles.AbstractHabilitetTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component


@Component
class HabilitetTeller(registry: MeterRegistry, accessor: Token) :
    AbstractHabilitetTeller(registry, accessor, "habilitet.oppslag.total", "Forsøk på å slå opp nærstående")

@Component
class HabilitetEgenDataTeller(registry: MeterRegistry, token: Token) :
    AbstractHabilitetTeller(registry, token, "habilitet.oppslag.egen", "Forsøk på å slå opp egen data")

@Component
class HabilitetFamilieTeller(registry: MeterRegistry, token: Token) :
    AbstractHabilitetTeller(registry, token, "habilitet.oppslag.familie", "Forsøk på å slå opp familie data")

@Component
class HabilitetSøskenTeller(registry: MeterRegistry, token: Token) :
    AbstractHabilitetTeller(registry, token, "habilitet.oppslag.søsken", "Forsøk på å slå opp søsken data")

@Component
class HabilitetFellesBarnTeller(registry: MeterRegistry, token: Token) :
    AbstractHabilitetTeller(registry, token, "habilitet.oppslag.fellesbarn", "Forsøk på å slå opp felles barn data")

@Component
class HabilitetPartnerTeller(registry: MeterRegistry, token: Token) :
    AbstractHabilitetTeller(registry, token, "habilitet.oppslag.partner", "Forsøk på å slå opp partner data")

@Component
class NasjonalGruppeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "gruppe.medlemskap.nasjonal", "Ansatte med og uten nasjonalt gruppemedlemsskap")

@Component
class AvdødTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "dead.oppslag.total", "Forsøk på å slå opp avdøde")

@Component
class OverstyringTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "overstyring.forsøk", "Overstyringsforsøk pr resultat")
