package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.felles.utils.Auditor
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
class EvalueringTeller(registry: MeterRegistry, token: Token, private val auditor: Auditor = Auditor()) :
    AbstractTeller(registry, token, "evaluering.resultat", "Evalueringsresultat")  {
    fun audit(message: String) = auditor.info(message)
    }

@Component
class BulkCacheTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "bulk.cache", "Hits/misses for bulk")

@Component
class CacheOppfriskerTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "cache.oppfrisker", "Antall oppfriskninger av cache etter utløp")

@Component
class BulkCacheSuksessTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "bulk.cache.suksess", "Hits/misses for entire bulk")

@Component
class TokenTypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "token.type", "Token type")

@Component
class OppfølgingskontorTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "oppfolging", "Oppfølgingskontor hits/misses")