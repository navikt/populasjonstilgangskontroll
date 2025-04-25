package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.felles.rest.ValidId
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor.Companion.AAD_ISSUER
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TilgangController", description = "Denne kontrolleren skal brukes i produksjon")
class TilgangController(
        private val regler: RegelTjeneste,
        private val overstyring: OverstyringTjeneste,
        private val token: TokenClaimsAccessor) {

    @PostMapping("komplett")
    @ResponseStatus(NO_CONTENT)
    fun kompletteRegler(@RequestBody @Valid @ValidId brukerId: String) =
        regler.kompletteRegler(token.ansattId!!, brukerId)

    @PostMapping("kjerne")
    @ResponseStatus(NO_CONTENT)
    fun kjerneregler(@RequestBody @Valid @ValidId brukerId: String) = regler.kjerneregler(token.ansattId!!, brukerId)

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    fun overstyr(@RequestBody data: OverstyringData) = overstyring.overstyr(token.ansattId!!, data)

    @PostMapping("bulk")
    @ResponseStatus(NO_CONTENT)
    fun bulk(@RequestBody @Valid @ValidId specs: Set<IdOgType>) = regler.bulkRegler(token.ansattId!!, specs)

}

