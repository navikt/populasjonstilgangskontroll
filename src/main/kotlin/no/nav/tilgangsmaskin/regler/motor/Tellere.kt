package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component



@Component
class NasjonalGruppeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "gruppe.medlemskap.nasjonal", "Ansatte med og uten nasjonalt gruppemedlemsskap")

@Component
class AvdødTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "dead.oppslag.total", "Forsøk på å slå opp avdøde")

@Component
class OverstyringTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "overstyring.forsøk", "Overstyringsforsøk pr resultat")

@Component
class AvvisningTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "avvist.regel", "Avvisningsbegrunnelse pr regel")

@Component
class RegeltypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "type.konsument", "Regeltype pr konsument")
@Component
class BulkCacheTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "bulk.cache", "Hits/misses for bulk")

@Component
class BulkCacheSuksessTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "bulk.cache.suksess", "Hits/misses for entire bulk")

@Component
class TokenTypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "token.type", "Token type")