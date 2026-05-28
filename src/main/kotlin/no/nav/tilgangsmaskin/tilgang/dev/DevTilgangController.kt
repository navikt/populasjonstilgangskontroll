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
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangGyldig
import no.nav.tilgangsmaskin.tilgang.ProblemDetailApiResponse
import no.nav.tilgangsmaskin.tilgang.dev.DevTilgangController.Companion.DEV_TILGANG_CONTROLLER_TAG_DESCRIPTION
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
@Tag(name = "DevTilgangController", description = DEV_TILGANG_CONTROLLER_TAG_DESCRIPTION)
class DevTilgangController(
    private val graphql: PdlSyncGraphQLClientAdapter,
    private val overstyring: EnkeltTilgangTjeneste,
    private val oppfølging: OppfølgingTjeneste,
    private val nom: NomJPAAdapter) {

    @PostMapping("oppfolging/{uuid}/avslutt")
    @Operation(summary = SUMMARY_OPPFOLGING_AVSLUTT, description = DESCRIPTION_OPPFOLGING_AVSLUTT)
    fun oppfølgingAvslutt(@RequestBody identer : Identer, @PathVariable uuid: UUID) =
        oppfølging.avslutt(Oppfølgingsendring.Avsluttet(uuid, identer))

    @GetMapping("oppfolging/enhet")
    @Operation(summary = SUMMARY_OPPFOLGING_ENHET, description = DESCRIPTION_OPPFOLGING_ENHET)
    fun enhetFor(@RequestParam id: Identifikator) =
        oppfølging.enhetFor(id)

    @GetMapping("sivilstand/{id}")
    @Operation(summary = SUMMARY_SIVILSTAND, description = DESCRIPTION_SIVILSTAND)
    fun sivilstand(@PathVariable  id: String) =
        graphql.partnere(id)

    @Operation(summary = SUMMARY_KOBLING, description = DESCRIPTION_KOBLING)
    @PostMapping("ansatt/{ansattId}/{brukerId}")
    @Transactional
    fun nom(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        nom.upsert(NomAnsattData(ansattId, brukerId))

    @GetMapping("nom/{ansattId}")
    @Operation(summary = SUMMARY_NOM_FNR, description = DESCRIPTION_NOM_FNR)
    fun nomFnr(@PathVariable ansattId: AnsattId) =
        nom.fnrForAnsatt(ansattId.verdi)


    @PostMapping("overstyr/{ansattId}")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    @Valid
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody  @Valid @EnkeltTilgangGyldig data: EnkeltTilgangData) =
        overstyring.overstyr(ansattId, data)

    @PostMapping("overstyringer/{ansattId}")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_HENT_OVERSTYRINGER, description = DESCRIPTION_HENT_OVERSTYRINGER)
    fun overstyringer(@PathVariable ansattId: AnsattId, @RequestBody brukerIds: List<BrukerId>) =
        overstyring.tilganger(ansattId, brukerIds)

    companion object {
        private const val DEV_TILGANG_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.tilgang.tag.description"

        private const val SUMMARY_OPPFOLGING_AVSLUTT = "msg:openapi.dev.tilgang.oppfolging.avslutt.summary"
        private const val DESCRIPTION_OPPFOLGING_AVSLUTT = "msg:openapi.dev.tilgang.oppfolging.avslutt.description"
        private const val SUMMARY_OPPFOLGING_ENHET = "msg:openapi.dev.tilgang.oppfolging.enhet.summary"
        private const val DESCRIPTION_OPPFOLGING_ENHET = "msg:openapi.dev.tilgang.oppfolging.enhet.description"
        private const val SUMMARY_SIVILSTAND = "msg:openapi.dev.tilgang.sivilstand.summary"
        private const val DESCRIPTION_SIVILSTAND = "msg:openapi.dev.tilgang.sivilstand.description"
        private const val SUMMARY_KOBLING = "msg:openapi.dev.tilgang.kobling.summary"
        private const val DESCRIPTION_KOBLING = "msg:openapi.dev.tilgang.kobling.description"
        private const val SUMMARY_NOM_FNR = "msg:openapi.dev.tilgang.nom.fnr.summary"
        private const val DESCRIPTION_NOM_FNR = "msg:openapi.dev.tilgang.nom.fnr.description"
        private const val SUMMARY_OVERSTYR = "msg:openapi.dev.tilgang.overstyr.summary"
        private const val DESCRIPTION_OVERSTYR = "msg:openapi.dev.tilgang.overstyr.description"
        private const val SUMMARY_HENT_OVERSTYRINGER = "msg:openapi.dev.tilgang.overstyringer.summary"
        private const val DESCRIPTION_HENT_OVERSTYRINGER = "msg:openapi.dev.tilgang.overstyringer.description"
    }
}
