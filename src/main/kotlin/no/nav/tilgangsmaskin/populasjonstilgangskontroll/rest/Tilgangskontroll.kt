package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class Tilgangskontroll {
    val tilgangsService = TilgangsService()


    @PostMapping("sjekkTilgang")
    fun sjekkTilgang(brukerIdent: String): TilgangsResponse{
        val harTilgang = tilgangsService.validerTilgang(brukerIdent)

        return harTilgang.let {
            TilgangsResponse(
                brukerIdent = brukerIdent,
                navIdent = "12345678911",
                ansatt_har_tilgang = it,
                begrunnelse = "Begrunnelse",
                begrunnbelse_kode = "BegrunnelseKode",
                kan_overstyres = true
            )
        }
    }

    @PostMapping("sjekkTilgangBulk")
    fun sjekkTilgangBulk(brukerIdenter: List<String> ): () -> List<TilgangsResponse> {
        return { tilgangsService.validerTilgangBulk(brukerIdenter) }
    }

}

data class TilgangsResponse(
    val brukerIdent: String,
    val navIdent: String,
    val ansatt_har_tilgang: Boolean,
    val begrunnelse: String,
    val begrunnbelse_kode: String,
    val kan_overstyres: Boolean
)