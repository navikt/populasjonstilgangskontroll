package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PdlGraphClient
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@ProtectedWithClaims(issuer = "azuread")
@RestController
@RequestMapping("/api/v1")
class Tilgangskontroll {
    val tilgangsService = TilgangsService(
        pdlGraphClient = PdlGraphClient(
            webClient = WebClient.builder().build()


        )
    )


    @PostMapping("sjekkTilgang")
    fun sjekkTilgang(bruker_ident: String): TilgangsResponse{
        //valider token.
        val nav_ident = "N999999" // Hent fra token
        val har_tilgang = tilgangsService.validerTilgang(bruker_ident, nav_ident)

        return har_tilgang.let {
            if (it) {
                TilgangsResponse(
                    bruker_ident = bruker_ident,
                    nav_ident = "N999999",
                    ansatt_har_tilgang = it,


                )
            } else {
                TilgangsResponse(
                    bruker_ident = bruker_ident,
                    nav_ident = "N999999",
                    ansatt_har_tilgang = it,
                    begrunnelse = Begrunnelse(begrunnelse= "Begrunnelse", begrunnelse_kode = "", kan_overstyres = false) //optional
                )
            }
        }
    }

    @PostMapping("sjekkTilgangBulk")
    fun sjekkTilgangBulk(brukerIdenter: List<String> ): () -> List<TilgangsResponse> {
        val navIdent = "N999999" // Hent fra token
        return { tilgangsService.validerTilgangBulk(brukerIdenter, navIdent) }
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