package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withAnsattContext
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.tilgang.BulkTilgangController.Companion.BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.felles.rest.TokenType.CCF
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import no.nav.tilgangsmaskin.felles.rest.TokenTypeTeller
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons
import no.nav.tilgangsmaskin.tilgang.openapi.BulkSwaggerApiRespons
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONTENT_TOO_LARGE
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@TilgangApiController
@ResponseStatus(MULTI_STATUS)
@Tag(name = "BulkTilgangController", description = BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION)
class BulkTilgangController(private val regelTjeneste: RegelTjeneste, token: Token, teller: TokenTypeTeller) : TilgangControllerBase(token, teller) {

    @PostMapping("bulk/obo")
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO)
    fun bulkOBO(@RequestBody specs: Set<BrukerIdOgRegelsett>, req: HttpServletRequest) =
        bulkOppslag(token.requiredAnsattId, OBO, specs, req.requestURI)

    @PostMapping("bulk/obo/{regelType}")
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO_REGELTYPE)
    fun bulkOBOForRegelType(@PathVariable regelType: RegelType, @RequestBody brukerIds: Set<String>, req: HttpServletRequest) =
        bulkOppslag(
            token.requiredAnsattId,
            OBO,
            brukerIds.mapTo(mutableSetOf()) { BrukerIdOgRegelsett(it, regelType) },
            req.requestURI
        )

    @PostMapping("bulk/ccf/{ansattId}")
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF)
    fun bulkCCF(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>, req: HttpServletRequest) =
        bulkOppslag( ansattId, CCF, specs, req.requestURI)

    @PostMapping("bulk/ccf/{ansattId}/{regelType}")
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF_REGELTYPE)
    fun bulkCCFForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody brukerIds: Set<String>, req: HttpServletRequest) =
        bulkOppslag(
            ansattId,
            CCF,
            brukerIds.mapTo(mutableSetOf()) { BrukerIdOgRegelsett(it, regelType) },
            req.requestURI
        )

    private fun bulkOppslag(ansatt: AnsattId, forventet: TokenType, specs: Set<BrukerIdOgRegelsett>, uri: String): AggregertBulkRespons {
        sjekk(token.type == forventet, FORBIDDEN, "Forventet token type $forventet for $uri, fikk ${token.type}")
        return withAnsattContext(ansatt) {
            if (specs.isNotEmpty()) {
                sjekk(specs.size <= 1000, CONTENT_TOO_LARGE, "Maksimalt 1000 brukerId-er kan sendes i en bulk forespørsel")
                sjekk(specs.none { it.brukerId.isBlank() }, BAD_REQUEST, "brukerId kan ikke være tom")
                tell("bulk", forventet)
                regelTjeneste.bulkRegler(ansatt, specs)
            } else {
                log.debug("Ingen brukerId-er oppgitt i bulk forespørsel for {}", ansatt)
                AggregertBulkRespons(ansatt)
            }
        }
    }

    companion object {
        private const val BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
        private const val SUMMARY_BULK = "${MSG}openapi.tilgang.bulk.summary"
        private const val DESCRIPTION_BULK_OBO = "${MSG}openapi.tilgang.bulk.obo.description"
        private const val DESCRIPTION_BULK_OBO_REGELTYPE = "${MSG}openapi.tilgang.bulk.obo.regeltype.description"
        private const val DESCRIPTION_BULK_CCF = "${MSG}openapi.tilgang.bulk.ccf.description"
        private const val DESCRIPTION_BULK_CCF_REGELTYPE = "${MSG}openapi.tilgang.bulk.ccf.regeltype.description"
    }
}
