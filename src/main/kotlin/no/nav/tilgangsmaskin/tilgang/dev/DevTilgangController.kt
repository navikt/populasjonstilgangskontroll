package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData
import no.nav.tilgangsmaskin.ansatt.nom.NomJPAAdapter
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.regler.overstyring.ValidOverstyring
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

private const val DEV_TILGANG_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.tilgang.tag.description"


@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
@Tag(name = "DevTilgangController", description = DEV_TILGANG_CONTROLLER_TAG_DESCRIPTION)
class DevTilgangController(
    private val graphql: PdlSyncGraphQLClientAdapter,
    private val overstyring: OverstyringTjeneste,
    private val oppfølging: OppfølgingTjeneste,
    private val nom: NomJPAAdapter) {

    @PostMapping("oppfolging/{uuid}/avslutt")
    fun oppfølgingAvslutt(@RequestBody identer : Identer, @PathVariable uuid: UUID) =
        oppfølging.avslutt(uuid, identer)

    @GetMapping("oppfolging/enhet")
    fun enhetFor(@RequestParam id: Identifikator) =
        oppfølging.enhetFor(id)

    @GetMapping("sivilstand/{id}")
    fun sivilstand(@PathVariable  id: String) =
        graphql.partnere(id)

    @Operation(summary = SUMMARY_KOBLING, description = DESCRIPTION_KOBLING)
    @PostMapping("ansatt/{ansattId}/{brukerId}")
    @Transactional
    fun nom(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        nom.upsert(NomAnsattData(ansattId, brukerId))

    @GetMapping("nom/{ansattId}")
    fun nomFnr(@PathVariable ansattId: AnsattId) =
        nom.fnrForAnsatt(ansattId.verdi)


    @PostMapping("overstyr/{ansattId}")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    @Valid
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody  @Valid @ValidOverstyring data: OverstyringData) =
        overstyring.overstyr(ansattId, data)

    @PostMapping("overstyringer/{ansattId}")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_HENT_OVERSTYRINGER, description = DESCRIPTION_HENT_OVERSTYRINGER)
    fun overstyringer(@PathVariable ansattId: AnsattId, @RequestBody brukerIds: List<BrukerId>) =
        overstyring.overstyringer(ansattId, brukerIds)

    companion object {
        const val SUMMARY_KOBLING = "msg:openapi.dev.tilgang.kobling.summary"
        const val DESCRIPTION_KOBLING = "msg:openapi.dev.tilgang.kobling.description"
        const val SUMMARY_OVERSTYR = "msg:openapi.dev.tilgang.overstyr.summary"
        const val DESCRIPTION_OVERSTYR = "msg:openapi.dev.tilgang.overstyr.description"
        const val SUMMARY_HENT_OVERSTYRINGER = "msg:openapi.dev.tilgang.overstyringer.summary"
        const val DESCRIPTION_HENT_OVERSTYRINGER = "msg:openapi.dev.tilgang.overstyringer.description"
    }
}
