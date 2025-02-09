package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenUtil
import org.springframework.web.bind.annotation.GetMapping
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenUtil.Companion.AAD_ISSUER
import org.slf4j.LoggerFactory.getLogger

@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearerAuth",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
class Tilgangskontroll(val service : RegelTjeneste, val ansatt: EntraTjeneste, private val tokenUtil: TokenUtil) {

    private val log = getLogger(Tilgangskontroll::class.java)

    @GetMapping("ansatt")
    @SecurityRequirement(name = "bearerAuth")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)

    @GetMapping("tilgang")
    @SecurityRequirement(name="bearerAuth")
    // TODO Gjør om til POST
    fun validerTilgang(kandidatId: Fødselsnummer) {
        tokenUtil.all.forEach { (k,v) -> log.info("$k->$v") }
        val id = tokenUtil.navIdentFromToken
        service.sjekkTilgang(id, kandidatId);
    }


}

