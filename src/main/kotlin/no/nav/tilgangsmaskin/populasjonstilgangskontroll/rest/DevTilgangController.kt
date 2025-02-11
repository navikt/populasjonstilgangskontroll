package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.KandidatTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(val kandidat : KandidatTjeneste, val ansatt: EntraTjeneste, val regler: RegelTjeneste)
{
    @GetMapping("kandidat")
    fun kandidat(fnr: Fødselsnummer) = kandidat.kandidat(fnr)

    @GetMapping("$GRAPH/ansatt")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)

    @GetMapping("$GRAPH/tilganger")
    fun hentAnsattTilganger(azureId : UUID) = ansatt.ansattTilganger(azureId)

    @GetMapping("regler")
    fun sjekkTilgang(@RequestParam saksbehandler: NavId, @RequestParam kandidat: Fødselsnummer) = regler.sjekkTilgang(saksbehandler, kandidat)

}