package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONTENT_TOO_LARGE
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.tilgang.tag.description"

@TilgangApiController
@ResponseStatus(MULTI_STATUS)
@Tag(name = "BulkTilgangController", description = BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION)
class BulkTilgangController(
    private val regelTjeneste: RegelTjeneste,
    guard: TokenTypeGuard,
    teller: TokenTypeTeller) : TilgangControllerBase(guard, teller) {

    @PostMapping("bulk/obo")
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO)
    fun bulkOBO(@RequestBody specs: Set<BrukerIdOgRegelsett>, req: HttpServletRequest) =
        bulkOppslag({ ansattIdFraToken() }, OBO, specs, req.requestURI)

    @PostMapping("bulk/obo/{regelType}")
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO_REGELTYPE)
    fun bulkOBOForRegelType(
        @PathVariable regelType: RegelType,
        @RequestBody brukerIds: Set<String>,
        req: HttpServletRequest,
    ) =
        bulkOppslag({ ansattIdFraToken() }, OBO, brukerIds.map { BrukerIdOgRegelsett(it, regelType) }.toSet(), req.requestURI)

    @PostMapping("bulk/ccf/{ansattId}")
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF)
    fun bulkCCF(
        @PathVariable ansattId: AnsattId,
        @RequestBody specs: Set<BrukerIdOgRegelsett>,
        req: HttpServletRequest,
    ) =
        bulkOppslag({ ansattId }, CCF, specs, req.requestURI)

    @PostMapping("bulk/ccf/{ansattId}/{regelType}")
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF_REGELTYPE)
    fun bulkCCFForRegelType(
        @PathVariable ansattId: AnsattId,
        @PathVariable regelType: RegelType,
        @RequestBody brukerIds: Set<String>,
        req: HttpServletRequest,
    ) =
        bulkOppslag({ ansattId }, CCF, brukerIds.map { BrukerIdOgRegelsett(it, regelType) }.toSet(), req.requestURI)

    private fun bulkOppslag(
        ansattId: () -> AnsattId,
        forventet: TokenType,
        specs: Set<BrukerIdOgRegelsett>,
        uri: String): AggregertBulkRespons {
        guard.krev(forventet, uri)
        val ansatt = ansattId()
        return withAnsattContext(ansatt) {
            if (specs.isNotEmpty()) {
                sjekk(specs.size <= 1000, CONTENT_TOO_LARGE, "Maksimalt 1000 brukerId-er kan sendes i en bulk forespørsel")
                sjekk(specs.none { it.brukerId.isBlank() }, BAD_REQUEST, "brukerId kan ikke være tom")
                tell("bulk", forventet)
                regelTjeneste.bulkRegler(ansatt, specs)
            } else {
                logBulkEmpty(ansatt)
                AggregertBulkRespons(ansatt)
            }
        }
    }

    private fun logBulkEmpty(ansatt: AnsattId) =
        log.debug("Ingen brukerId-er oppgitt i bulk forespørsel for {}", ansatt)

    companion object {
        private const val SUMMARY_BULK = "msg:openapi.tilgang.bulk.summary"
        private const val DESCRIPTION_BULK_OBO = "msg:openapi.tilgang.bulk.obo.description"
        private const val DESCRIPTION_BULK_OBO_REGELTYPE = "msg:openapi.tilgang.bulk.obo.regeltype.description"
        private const val DESCRIPTION_BULK_CCF = "msg:openapi.tilgang.bulk.ccf.description"
        private const val DESCRIPTION_BULK_CCF_REGELTYPE = "msg:openapi.tilgang.bulk.ccf.regeltype.description"
    }
}
