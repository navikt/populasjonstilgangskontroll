package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringData
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.IdOgType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor.Companion.AAD_ISSUER
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TilgangController", description = "Denne kontrolleren skal brukes i produksjon")
class TilgangController(private val regler : RegelTjeneste, private val overstyring: OverstyringTjeneste,private val token: TokenClaimsAccessor) {

    @PostMapping("komplett")
    @ResponseStatus(NO_CONTENT)
    fun kompletteRegler(@RequestBody brukerId: BrukerId)  = regler.kompletteRegler(token.ansattId!!, brukerId)

    @PostMapping("kjerne")
    @ResponseStatus(NO_CONTENT)
    fun kjerneregler(@RequestBody brukerId: BrukerId) = regler.kjerneregler(token.ansattId!!, brukerId)

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    fun overstyr(@RequestBody data: OverstyringData) = overstyring.overstyr(token.ansattId!!,data)

    @PostMapping("bulk")
    @ResponseStatus(NO_CONTENT)
    fun bulk(@RequestBody  specs: List<IdOgType>) = regler.bulkRegler(token.ansattId!!, specs)

}

