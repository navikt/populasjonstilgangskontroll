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
    fun sjekkTilgang(brukerIdent: String): String {
        val harTilgang = tilgangsService.validerTilgang(brukerIdent)

        return harTilgang.toString()
    }
}