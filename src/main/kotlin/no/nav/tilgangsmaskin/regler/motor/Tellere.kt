package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component
import java.util.Locale.getDefault

@Component
class NasjonalGruppeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "gruppe.medlemskap.nasjonal", "Ansatte med og uten nasjonalt gruppemedlemsskap")

@Component
class AvdødTeller(registry: MeterRegistry, accessor: Token) :
    AbstractTeller(registry, accessor, "dead.oppslag.total", "Forsøk på å slå opp avdøde") {
    fun tell(intervall: Dødsperiode, enhet: String) =
        tell(Tags.of(MÅNEDER, intervall.tekst, ENHET, enhet))

    private companion object {
        private const val ENHET = "enhet"
        private const val MÅNEDER = "months"
    }
}
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
    AbstractTeller(registry, token, "bulk.cache", "Cache treff/bom for bulk")

@Component
class CacheOppfriskerTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "cache.oppfrisker", "Antall oppfriskninger av cache etter utløp")

@Component
class BulkCacheSuksessTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "bulk.cache.suksess", "Treff/bom for hele bulken")

@Component
class TokenTypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "token.type", "Token type")

@Component
class OppfølgingkontorTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "oppfolging", "Oppfølgingkontor treff/bom")

@Component
class OppfriskingTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "endringer", "Endrede user oids")

@Component
class PdlCacheTømmerTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, NAVN, "Cache tømming pr beskyttelsesgrad") {
    fun tell(cache: CachableConfig,gradering: String, endringsType: String) =
        tell(Tags.of(CACHE, cache.name,GRADERING ,
            gradering.lowercase(getDefault()),TYPE,endringsType))

    private companion object {
        private const val GRADERING = "gradering"
        private const val CACHE = "cache"
        private const val TYPE = "type"
        private const val NAVN = "beskyttelse"
    }
}


abstract class AbstractTeller(
    private val registry: MeterRegistry,
    private val token: Token,
    private val navn: String,
    private val beskrivelse: String) {

    fun tell(vararg tags: Tag, n: Int = 1) =
        tell(Tags.of(*tags), n)

    fun tell(tags: Tags = Tags.empty(), n: Int = 1) =
        Counter.builder(navn)
            .description(beskrivelse)
            .tags(tags
                .and("system", token.system)
                .and("clustersystem", token.clusterAndSystem))
            .register(registry)
            .increment(n.toDouble())
}