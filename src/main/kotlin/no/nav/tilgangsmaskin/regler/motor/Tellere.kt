package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
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
class EvalueringTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "evaluering.resultat", "Evalueringsresultat pr begrunnelse")
@Component
class EvalueringTypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "evalueringtype.resultat", "Evalueringsresultat pr type og begrunnelse")

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
class OppfølgingkontorTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "oppfolging", "Oppfølgingkontor hits/misses")

@Component
class OppfriskingTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "endringer", "Endrede user oids")

@Component
class PdlCacheTømmerTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "beskyttelse", "Cache tømming pr beskyttelsesgrad")


abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val navn: String,
        private val beskrivelse: String) {


    open fun tell(vararg tags: Tag, n:Int=1) =
        tell(Tags.of(*tags), n)

    open fun tell(tags: Tags = Tags.empty(), n:Int=1) =
        Counter.builder(navn)
            .description(beskrivelse)
            .tags(tags
                .and("system", token.system)
                .and("clustersystem", token.clusterAndSystem))
            .register(registry)
            .increment(n.toDouble())
}