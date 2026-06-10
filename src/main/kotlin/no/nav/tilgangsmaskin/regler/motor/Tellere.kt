package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component
import java.util.Locale.*

@Component
class Tellere(registry: MeterRegistry, token: Token) {
    val nasjonalGruppe = NasjonalGruppeTeller(registry, token)
    val enkelttilgang = EnkeltTilgangTeller(registry, token)
    val evaluering = EvalueringTeller(registry, token)
    val evalueringType = EvalueringTypeTeller(registry, token)
    val bulkCache = BulkCacheTeller(registry, token)
    val cacheOppfrisker = CacheOppfriskerTeller(registry, token)
    val bulkCacheSuksess = BulkCacheSuksessTeller(registry, token)
    val tokenType = TokenTypeTeller(registry, token)
    val oppfølgingkontor = OppfølgingkontorTeller(registry, token)
    val oppfrisking = OppfriskingTeller(registry, token)
    val pdlCacheTømmer = PdlCacheTømmerTeller(registry, token)
}

class NasjonalGruppeTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "gruppe.medlemskap.nasjonal", "Ansatte med og uten nasjonalt gruppemedlemsskap")

class EnkeltTilgangTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "overstyring.forsøk", "Enkelttilgang forsøk pr resultat")

class EvalueringTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "evaluering.resultat", "Evalueringsresultat pr begrunnelse")

class EvalueringTypeTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "evalueringtype.resultat", "Evalueringsresultat pr type og begrunnelse")

class BulkCacheTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "bulk.cache", "Cache treff/bom for bulk")

class CacheOppfriskerTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "cache.oppfrisker", "Antall oppfriskninger av cache etter utløp")

class BulkCacheSuksessTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "bulk.cache.suksess", "Treff/bom for hele bulken")

class TokenTypeTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "token.type", "Token type")

class OppfølgingkontorTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "oppfolging", "Oppfølgingkontor treff/bom")

class OppfriskingTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "endringer", "Endrede user oids")

class PdlCacheTømmerTeller(registry: MeterRegistry, token: Token) :
    Teller(registry, token, "beskyttelse", "Cache tømming pr beskyttelsesgrad") {

    fun tell(cache: CacheNøkkelConfig, gradering: String, endringsType: String) =
        tell(Tags.of("cache", cache.name, "gradering",
            gradering.lowercase(getDefault()), "type", endringsType))
}

open class Teller(
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

abstract class AbstractAsyncTeller(
    private val registry: MeterRegistry,
    private val navn: String,
    private val beskrivelse: String) {

    fun tell(n: Int = 1) =
        Counter.builder(navn)
            .description(beskrivelse)
            .register(registry)
            .increment(n.toDouble())
}
