package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData
import no.nav.tilgangsmaskin.ansatt.nom.NomJPAAdapter
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.felles.rest.ValidOverstyring
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*


@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
@Tag(name = "DevTilgangController", description = "Denne kontrolleren skal kun brukes til testing")
class DevTilgangController(
    private val graphql: PdlSyncGraphQLClientAdapter,
    private val skjerming: SkjermingTjeneste,
    private val skjermingAdapter: SkjermingRestClientAdapter,
    private val overstyring: OverstyringTjeneste,
    private val oppfølging: OppfølgingTjeneste,
    private val nom: NomJPAAdapter) {
    
    @PostMapping("oppfolging/{uuid}/{kontor}/registrer")
    fun registrer(@RequestBody identer : Identer,@PathVariable uuid: UUID, @PathVariable kontor: Enhetsnummer) =
        oppfølging.registrer(uuid, identer, Kontor(kontor))

    @PostMapping("oppfolging/{uuid}/avslutt")
    fun oppfølgingAvslutt(@RequestBody identer : Identer, @PathVariable uuid: UUID) = oppfølging.avslutt(uuid, identer)

    @GetMapping("oppfolging/enhet")
    fun enhetFor(@RequestParam id: Identifikator) = oppfølging.enhetFor(id)



    @GetMapping("sivilstand/{id}")
    fun sivilstand(@PathVariable  id: String) = graphql.partnere(id)


    @Operation(
        summary= "Sette kobling mellom ansatt og fnr",
        description = """Funksjon for å opprette relasjon mellom nav-ident og fnrslik  at oppslag på egne data og familierelasjoner kan testes """)
    @PostMapping("ansatt/{ansattId}/{brukerId}")
    fun nom(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        nom.upsert(NomAnsattData(ansattId, brukerId))

    @GetMapping("nom/{ansattId}")
    fun nomFnr(@PathVariable ansattId: AnsattId) =
        nom.fnrForAnsatt(ansattId.verdi)


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


    @PostMapping("skjermingadaptere")
    fun skjermingAdapter(@RequestBody brukerId: String) = skjermingAdapter.skjerming(brukerId)

    @PostMapping("skjerming")
    fun skjerming(@RequestBody brukerId: BrukerId) = skjerming.skjerming(brukerId)

    @PostMapping("skjerminger")
    fun skjerminger(@RequestBody ids: List<BrukerId>) = skjerming.skjerminger(ids)


}
