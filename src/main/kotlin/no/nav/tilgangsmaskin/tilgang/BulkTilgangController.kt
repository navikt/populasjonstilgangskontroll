package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.Validator
import jakarta.validation.constraints.NotBlank
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.ProdController
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.security.OAuth2RequireCCF
import no.nav.tilgangsmaskin.felles.security.OAuth2RequireOBO
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withAnsattContext
import org.slf4j.LoggerFactory.getLogger
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons
import no.nav.tilgangsmaskin.tilgang.openapi.BulkSwaggerApiRespons
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONTENT_TOO_LARGE
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
private const val SUMMARY_BULK = "${MSG}openapi.tilgang.bulk.summary"
private const val DESCRIPTION_BULK_OBO = "${MSG}openapi.tilgang.bulk.obo.description"
private const val DESCRIPTION_BULK_OBO_REGELTYPE = "${MSG}openapi.tilgang.bulk.obo.regeltype.description"
private const val DESCRIPTION_BULK_CCF = "${MSG}openapi.tilgang.bulk.ccf.description"
private const val DESCRIPTION_BULK_CCF_REGELTYPE = "${MSG}openapi.tilgang.bulk.ccf.regeltype.description"


@ProdController
@ResponseStatus(MULTI_STATUS)
@Tag(name = "BulkTilgangController", description = BULK_TILGANG_CONTROLLER_TAG_DESCRIPTION)
class BulkTilgangController(
    private val regelTjeneste: RegelTjeneste,
    private val validator: Validator,
    private val token: Token
) {

    private val log = getLogger(javaClass)

    @PostMapping("bulk/obo")
    @OAuth2RequireOBO
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO)
    fun bulkOBO(@RequestBody @Valid specs: Set<@Valid BrukerIdOgRegelsett>) =
        bulkOppslag(token.requiredAnsattId, specs)

    @PostMapping("bulk/obo/{regelType}")
    @OAuth2RequireOBO
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO_REGELTYPE)
    fun bulkOBOForRegelType(
        @PathVariable regelType: RegelType,
        @RequestBody @Valid brukerIds: Set<@NotBlank(message = "brukerId kan ikke være tom") String>
    ) =
        bulkOppslag(
            token.requiredAnsattId,
            brukerIds.mapTo(mutableSetOf()) { BrukerIdOgRegelsett(it, regelType) }
        )

    @PostMapping("bulk/ccf/{ansattId}")
    @OAuth2RequireCCF
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF)
    fun bulkCCF(@PathVariable ansattId: AnsattId, @RequestBody @Valid specs: Set<@Valid BrukerIdOgRegelsett>) =
        bulkOppslag(ansattId, specs)

    @PostMapping("bulk/ccf/{ansattId}/{regelType}")
    @OAuth2RequireCCF
    @BulkSwaggerApiRespons(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF_REGELTYPE)
    fun bulkCCFForRegelType(
        @PathVariable ansattId: AnsattId,
        @PathVariable regelType: RegelType,
        @RequestBody @Valid brukerIds: Set<@NotBlank(message = "brukerId kan ikke være tom") String>) =
        bulkOppslag(
            ansattId,
            brukerIds.mapTo(mutableSetOf()) { BrukerIdOgRegelsett(it, regelType) }
        )

    private fun bulkOppslag(ansatt: AnsattId, specs: Set<BrukerIdOgRegelsett>): AggregertBulkRespons {
        return withAnsattContext(ansatt) {
            if (specs.isNotEmpty()) {
                sjekk(specs.size <= 1000, CONTENT_TOO_LARGE, "Maksimalt 1000 brukerId-er kan sendes i en bulk forespørsel")
                sjekk(specs.none { it.brukerId.isBlank() }, BAD_REQUEST, "brukerId kan ikke være tom")
                valider(specs)
                regelTjeneste.bulkRegler(ansatt, specs)
            } else {
                log.debug("Ingen brukerId-er oppgitt i bulk forespørsel for {}", ansatt)
                AggregertBulkRespons(ansatt)
            }
        }
    }

    private fun valider(specs: Set<BrukerIdOgRegelsett>) {
        specs.asSequence()
            .flatMap { validator.validate(it).asSequence() }
            .firstOrNull()
            ?.let { sjekk(false, BAD_REQUEST, it.message) }
    }
}
