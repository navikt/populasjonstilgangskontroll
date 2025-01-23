package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest


import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = "azuread")
@RestController
@RequestMapping("/api/v1")
class Tilgangskontroll(val service : TilgangsService) {

    @PostMapping("sjekkTilgang")
    fun sjekkTilgang(bruker_ident: String): TilgangsResponse{
        //valider token.
        val nav_ident = "N999999" // Hent fra token
                return TilgangsResponse(
                    bruker_ident = bruker_ident,
                    nav_ident = "N999999",
                    ansatt_har_tilgang = true,
                    begrunnelse = Begrunnelse(begrunnelse= "Begrunnelse", begrunnelse_kode = "", kan_overstyres = false) //optional
                )
            }

}

data class TilgangsResponse(
    val bruker_ident: String,
    val nav_ident: String,
    val ansatt_har_tilgang: Boolean,
    val begrunnelse: Begrunnelse? = null,

)
data class Begrunnelse(
    val begrunnelse: String,
    val begrunnelse_kode: String,
    val kan_overstyres: Boolean
)