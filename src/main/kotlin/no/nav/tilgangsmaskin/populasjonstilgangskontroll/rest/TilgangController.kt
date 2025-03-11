package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelSpec
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor.Companion.AAD_ISSUER
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
class TilgangController(private val regler : RegelTjeneste, private val overstyringTjeneste: OverstyringTjeneste,private val token: TokenClaimsAccessor) {

    @PostMapping("komplett")
    @ResponseStatus(NO_CONTENT)
    fun kompletteRegler(@RequestBody brukerId: BrukerId)  = regler.kompletteRegler(token.ansattId, brukerId)

    @PostMapping("kjerne")
    @ResponseStatus(NO_CONTENT)
    fun kjerneregler(@RequestBody brukerId: BrukerId) = regler.kjerneregler(token.ansattId, brukerId)

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    fun overstyr(@RequestBody data: OverstyringData) = overstyringTjeneste.overstyr(token.ansattId,data)

    @PostMapping("bulk")
    @ResponseStatus(NO_CONTENT)
    fun bulk(@RequestBody  specs: List<RegelSpec>) = regler.bulkRegler(token.ansattId, specs)

}

