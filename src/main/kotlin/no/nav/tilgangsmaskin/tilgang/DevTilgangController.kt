package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.felles.rest.ValidId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.ProblemDetailApiResponse
import no.nav.tilgangsmaskin.tilgang.BulkApiResponse
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
@Tag(name = "DevTilgangController", description = "Denne kontrolleren skal kun brukes til testing")
class DevTilgangController(
    private val graphql: PdlSyncGraphQLClientAdapter,
    private val skjerming: SkjermingTjeneste,
    private val skjermingAdapter: SkjermingRestClientAdapter,
    private val brukere: BrukerTjeneste,
    private val ansatte: AnsattTjeneste,
    private val entra: EntraTjeneste,
    private val regler: RegelTjeneste,
    private val overstyring: OverstyringTjeneste,
    private val pip: PdlRestClientAdapter,
    private val nom: NomTjeneste,
    private val pdl: PDLTjeneste) {

    private  val log = getLogger(javaClass)

    @GetMapping("resolve/{ansattId}")
    fun resolve(@PathVariable ansattId: AnsattId) = entra.resolve(ansattId)

    @GetMapping("sivilstand/{id}")
    fun sivilstand(@PathVariable @Valid @ValidId id: String) = graphql.sivilstand(id)

    @GetMapping("bruker/{id}")
    fun bruker(@PathVariable @Valid @ValidId id: String) = brukere.brukerMedUtvidetFamilie(id)

    @GetMapping("person/{id}")
    fun person(@PathVariable @Valid @ValidId id: String) = pdl.medUtvidetFamile(id)

    @GetMapping("person/pip/{id}")
    fun pip(@PathVariable @Valid @ValidId id: String) = pip.person(id)

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
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable @Valid @ValidId brukerId: String) =
        regler.kompletteRegler(ansattId, brukerId.trim('"'))

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable @Valid @ValidId brukerId: String) =
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
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody @Valid data: OverstyringData) = overstyring.overstyr(ansattId, data)

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
    @BulkApiResponse
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody @Valid @ValidId specs: Set<BrukerIdOgRegelsett>) =
        regler.bulkRegler( ansattId, specs)

    @PostMapping("bulk/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkApiResponse
    fun bulkreglerForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody @Valid @ValidId brukerIds: Set<BrukerId>) =
        regler.bulkRegler(ansattId, brukerIds.map { BrukerIdOgRegelsett(it,regelType) }.toSet())

    @PostMapping("skjermingadaptere")
    fun skjermingAdapter(@RequestBody brukerId: String) = skjermingAdapter.skjerming(brukerId)

    @PostMapping("skjerming")
    fun skjerming(@RequestBody brukerId: BrukerId) = skjerming.skjerming(brukerId)

    @PostMapping("skjerminger")
    fun skjerminger(@RequestBody ids: Set<BrukerId>) = skjerming.skjerminger(ids)

    @PostMapping("brukere")
    fun brukere(@RequestBody @Valid @ValidId  ids: Set<String>) = brukere.brukere(ids)
}
