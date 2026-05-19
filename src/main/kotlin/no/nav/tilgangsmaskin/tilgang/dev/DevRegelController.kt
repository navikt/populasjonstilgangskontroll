package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.tilgang.BulkSwaggerApiRespons
import no.nav.tilgangsmaskin.tilgang.ProblemDetailApiResponse
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val DEV_REGEL_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.regel.tag.description"

@UnprotectedRestController(value = ["/${DEV}/regel/"])
@ConditionalOnNotProd
@Tag(name = "DevRegelController", description = DEV_REGEL_CONTROLLER_TAG_DESCRIPTION)
class DevRegelController(private val regler: RegelTjeneste) {
    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KOMPLETT, description = DESCRIPTION_KOMPLETT)
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kompletteRegler(ansattId, brukerId.trim('"'))

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KJERNE, description = DESCRIPTION_KJERNE)
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kjerneregler(ansattId, brukerId.trim('"'))

    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK)
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>) =
        regler.bulkRegler( ansattId, specs)

    @PostMapping("bulk/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK_REGELTYPE, description = DESCRIPTION_BULK_REGELTYPE)
    fun bulkreglerForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody brukerIds: Set<BrukerId>) =
        regler.bulkRegler(ansattId, brukerIds.map { BrukerIdOgRegelsett(it.verdi, regelType) }.toSet())

    companion object {
        private const val SUMMARY_KOMPLETT = "msg:openapi.dev.regel.komplett.summary"
        private const val DESCRIPTION_KOMPLETT = "msg:openapi.dev.regel.komplett.description"
        private const val SUMMARY_KJERNE = "msg:openapi.dev.regel.kjerne.summary"
        private const val DESCRIPTION_KJERNE = "msg:openapi.dev.regel.kjerne.description"
        private const val SUMMARY_BULK = "msg:openapi.dev.regel.bulk.summary"
        private const val DESCRIPTION_BULK = "msg:openapi.dev.regel.bulk.description"
        private const val SUMMARY_BULK_REGELTYPE = "msg:openapi.dev.regel.bulk.regeltype.summary"
        private const val DESCRIPTION_BULK_REGELTYPE = "msg:openapi.dev.regel.bulk.regeltype.description"
    }

}