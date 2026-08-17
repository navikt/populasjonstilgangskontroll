package no.nav.tilgangsmaskin.regler

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.tilgang.openapi.BulkSwaggerApiRespons
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val DEV_REGEL_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.regel.tag.description"
private const val SUMMARY_KOMPLETT = "${MSG}openapi.dev.regel.komplett.summary"
private const val DESCRIPTION_KOMPLETT = "${MSG}openapi.dev.regel.komplett.description"
private const val SUMMARY_KJERNE = "${MSG}openapi.dev.regel.kjerne.summary"
private const val DESCRIPTION_KJERNE = "${MSG}openapi.dev.regel.kjerne.description"
private const val SUMMARY_BULK = "${MSG}openapi.dev.regel.bulk.summary"
private const val DESCRIPTION_BULK = "${MSG}openapi.dev.regel.bulk.description"
private const val SUMMARY_BULK_REGELTYPE = "${MSG}openapi.dev.regel.bulk.regeltype.summary"
private const val DESCRIPTION_BULK_REGELTYPE = "${MSG}openapi.dev.regel.bulk.regeltype.description"


@DevController(
    value = ["/${DEV}/regel/"],
    name = "DevRegelController",
    description = DEV_REGEL_CONTROLLER_TAG_DESCRIPTION
)
class RegelController(private val regler: RegelTjeneste) {
    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse(summary = SUMMARY_KOMPLETT, description = DESCRIPTION_KOMPLETT)
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kompletteRegler(ansattId, brukerId.trim('"'))

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse(summary = SUMMARY_KJERNE, description = DESCRIPTION_KJERNE)
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kjerneregler(ansattId, brukerId.trim('"'))

    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK)
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>) =
        regler.bulkRegler(ansattId, specs)

    @PostMapping("bulk/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK_REGELTYPE, description = DESCRIPTION_BULK_REGELTYPE)
    fun bulkreglerForRegelType(@PathVariable ansattId: AnsattId,
                               @PathVariable regelType: RegelType,
                               @RequestBody brukerIds: Set<BrukerId>) =
        regler.bulkRegler(ansattId, brukerIds.map { BrukerIdOgRegelsett(it.verdi, regelType) }.toSet())
}