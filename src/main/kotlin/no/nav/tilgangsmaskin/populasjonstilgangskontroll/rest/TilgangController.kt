package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringMetadata
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor.Companion.AAD_ISSUER
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP )
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
class TilgangController(private val regler : RegelTjeneste, private val overstyringTjeneste: OverstyringTjeneste,private val token: TokenAccessor) {

    @PostMapping("tilgang")
    fun alleRegler(@RequestBody brukerId: BrukerId) = regler.alleRegler(token.ansattId, brukerId)

    @PostMapping("kjerneregler")
    fun kjerneregler(@RequestBody brukerId: BrukerId) = regler.kjerneregler(token.ansattId, brukerId)

    @PostMapping("overstyr/{ansattId}/{brukerId}")
    fun overstyr(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId, @RequestBody metadata: OverstyringMetadata): ResponseEntity<Unit> {
        overstyringTjeneste.overstyr(ansattId, brukerId, metadata)
        return ResponseEntity.accepted().build()
    }
}

