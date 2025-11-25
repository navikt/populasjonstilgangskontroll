package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.rest.ValidOverstyring
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.BulkSwaggerApiRespons
import no.nav.tilgangsmaskin.tilgang.ProblemDetailApiResponse
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
@Tag(name = "DevTilgangController", description = "Denne kontrolleren skal kun brukes til testing")
class DevTilgangController(
    private val graphql: PdlSyncGraphQLClientAdapter,
    private val skjerming: SkjermingTjeneste,
    private val skjermingAdapter: SkjermingRestClientAdapter,
    private val brukere: BrukerTjeneste,
    private val ansatte: AnsattTjeneste,
    private val regler: RegelTjeneste,
    private val entra: EntraTjeneste,
    private val overstyring: OverstyringTjeneste,
    private val oppfølging: OppfølgingTjeneste,
    private val pip: PdlRestClientAdapter,
    private val oid: AnsattOidTjeneste,
    private val nom: NomTjeneste,
    private val pdl: PDLTjeneste,
    private val cache: CacheClient) {

    @PostMapping("oppfolging/bulk")
    fun oppfolgingEnhet(@RequestBody brukerId: Identifikator) = oppfølging.enhetFor(brukerId.verdi)

    @PostMapping("cache/skjerminger")
    fun cacheSkjerminger(@RequestBody  navIds: Set<String>) = cache.getMany<Boolean>(CachableConfig(SKJERMING),navIds)

    @PostMapping("cache/personer")
    fun cachePersoner(@RequestBody  navIds: Set<Identifikator>) = cache.getMany<Person>(CachableConfig(PDL),navIds.map { it.verdi }.toSet())

    @GetMapping("cache/keys/{cacheName}")
    fun keys(@PathVariable cacheName: String) = cache.getAll(cacheName)

    @GetMapping("sivilstand/{id}")
    fun sivilstand(@PathVariable  id: String) = graphql.partnere(id)

    @PostMapping("brukeridentifikator")
    fun brukerIdentifikator(@RequestBody id: Identifikator) = brukere.brukerMedUtvidetFamilie(id.verdi)

    @GetMapping("bruker/{id}")
    fun bruker(@PathVariable id: String) = brukere.brukerMedUtvidetFamilie(id)

    @GetMapping("person/{id}")
    fun person(@PathVariable id: String) = pdl.medUtvidetFamile(id)

    @GetMapping("person/pip/{id}")
    fun pip(@PathVariable id: String) = pip.person(id)

    @GetMapping("ansatt/enheter/{ansattId}")
    fun enheter(@PathVariable ansattId: AnsattId) = entra.geoOgGlobaleGrupper(ansattId, oid.oidFraEntra(ansattId)).filter { it.displayName.contains("ENHET") }

    @GetMapping("ansatt/{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatte.ansatt(ansattId)

    @PostMapping("ansatt/{ansattId}/{brukerId}")
    fun nom(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        nom.lagre(NomAnsattData(ansattId, brukerId))
    @Operation(
        summary= "Sette kobling mellom ansatt og fnr",
        description = """Funksjon for å opprette relasjon mellom nav-ident og fnrslik  at oppslag på egne data og familierelasjoner kan testes """)

    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kompletteRegler(ansattId, brukerId.trim('"'))

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kjerneregler(ansattId, brukerId.trim('"'))

    @PostMapping("overstyr/{ansattId}")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(
        summary = "Overstyr regler for en bruker",
        description = """Setter overstyring for en bruker, slik at den kan saksbehandles selv om saksbehandler opprinnelig ikke har tilgang.
                BrukerId må være gyldig og finnes i PDL. Kjerneregelsettet vil bli kjørt før overstyring, og hvis de feiler vil overstyring ikke bli gjort.
                Overstyring vil gjelde frem til og med utløpsdatoen."""
    )
    @Valid
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody  @Valid @ValidOverstyring  data: OverstyringData) = overstyring.overstyr(ansattId, data)

    @PostMapping("overstyringer/{ansattId}")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(
        summary = "Hent overstyringer for en ansatt og en eller flere brukere",
        description = "Henter overstyringer for en eller flere  brukere."
    )
    fun overstyringer(@PathVariable ansattId: AnsattId, @RequestBody brukerIds: List<BrukerId>) = overstyring.overstyringer(ansattId, brukerIds)

    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>) =
        regler.bulkRegler( ansattId, specs)

    @PostMapping("bulk/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    fun bulkreglerForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody brukerIds: Set<BrukerId>) =
        regler.bulkRegler(ansattId, brukerIds.map { BrukerIdOgRegelsett(it.verdi, regelType) }.toSet())

    @PostMapping("skjermingadaptere")
    fun skjermingAdapter(@RequestBody brukerId: String) = skjermingAdapter.skjerming(brukerId)

    @PostMapping("skjerming")
    fun skjerming(@RequestBody brukerId: BrukerId) = skjerming.skjerming(brukerId)

    @PostMapping("skjerminger")
    fun skjerminger(@RequestBody ids: List<BrukerId>) = skjerming.skjerminger(ids)

    @PostMapping("brukere")
    fun brukere(@RequestBody ids: Set<String>) = brukere.brukere(ids)

    @GetMapping("version")
    fun v1() = "1.0-dev"

    @GetMapping("version", version = "2.0")
    fun v2() = "2.0-dev"
}
