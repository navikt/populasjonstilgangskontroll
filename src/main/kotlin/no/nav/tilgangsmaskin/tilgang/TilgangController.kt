package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.ValidId
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgType
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AAD_ISSUER
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TilgangController", description = "Denne kontrolleren skal brukes i produksjon")
class TilgangController(
        private val regler: RegelTjeneste,
        private val overstyring: OverstyringTjeneste,
        private val token: Token) {

    @PostMapping("komplett")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = "Evaluer et komplett regelsett for en bruker")
    fun kompletteRegler(@RequestBody @Valid @ValidId brukerId: String) = regler.kompletteRegler(token.ansattId!!, brukerId.trim('"'))

    @PostMapping("kjerne")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = "Evaluer et kjerneregelsett for en bruker")
    fun kjerneregler(@RequestBody @Valid @ValidId brukerId: String) = regler.kjerneregler(token.ansattId!!, brukerId.trim('"'))

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = "Overstyr regler for en bruker",
        description =  """Setter overstyring for en bruker, slik at den kan saksbehandles selv om tilgang opprinnelig avslås.
    BrukerId må være gyldig og finnes i PDL. Kjerneregelsettet vil bli kjørt før overstyring, og hvis de feiler vil overstyring ikke bli gjort.
    Overstyring vil gjelde frem til og med utløpsdatoen."""
    )
    fun overstyr(@RequestBody data: OverstyringData) = overstyring.overstyr(token.ansattId!!, data)

    @PostMapping("bulk")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailBulkApiResponse
    @Operation(summary = "Kjør bulkregler for en ansatt",
        description = "Dette endepunktet er kun tilgjengelig for obo flow. " +
                "Det evaluerer regler for en ansatt mot et sett av brukerId-er og regeltyper. Om ingen regeltype oppgis, evalueres det komplette regelsettet")

    fun bulkOBO(@RequestBody @Valid @ValidId specs: Set<BrukerIdOgType>) =
        if (!token.erObo) {
            throw ResponseStatusException(FORBIDDEN, "Dette endepunkt er kun tilgjengelig for obo flow.")
        }
        else regler.bulkRegler(token.ansattId!!, specs)


    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailBulkApiResponse
    @Operation(summary = "Kjør bulkregler for en ansatt",
        description = "Dette endepunktet er kun tilgjengelig for client credentials flow. " +
                "Det evaluerer regler for en ansatt mot et sett av brukerId-er og regeltyper. Om ingen regeltype oppgis, evalueres det komplette regelsettet")
    fun bulkCCF(@PathVariable ansattId: AnsattId, @RequestBody @Valid @ValidId specs: Set<BrukerIdOgType>) =
        if (!token.erCC) {
            throw ResponseStatusException(FORBIDDEN, "Dette endepunkt er kun tilgjengelig client credentials-flow.")
        }
        else {
            if (specs.size > 1000) {
                throw ResponseStatusException(
                    PAYLOAD_TOO_LARGE,
                    "Maksimalt 1000 brukerId-er kan sendes i bulk-regler. Antall mottatt: ${specs.size}"
                )
            }
            regler.bulkRegler(ansattId, specs)
        }
}


