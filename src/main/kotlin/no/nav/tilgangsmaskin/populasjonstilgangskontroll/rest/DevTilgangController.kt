package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.KandidatTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.SaksbehandlerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(val kandidat : KandidatTjeneste, private val saksbehandler: SaksbehandlerTjeneste, val regler: RegelTjeneste)
{
    @GetMapping("kandidat")
    fun kandidat(fnr: Fødselsnummer) = kandidat.kandidat(fnr)

    @GetMapping("saksbehandler")
    fun saksbehandler(navId: NavId) = saksbehandler.saksbehandler(navId)

    @GetMapping("regler")
    fun sjekkTilgang(@RequestParam saksbehandler: NavId, @RequestParam kandidat: Fødselsnummer) = regler.sjekkTilgang(saksbehandler, kandidat)

}