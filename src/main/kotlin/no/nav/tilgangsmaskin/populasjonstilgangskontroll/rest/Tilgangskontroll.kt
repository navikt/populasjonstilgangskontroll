package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PersonTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@ProtectedRestController(value = ["/api/v1"], issuer = "azuread", claimMap = [])
class Tilgangskontroll(val service : TilgangsTjeneste) {


}

@UnprotectedRestController(value = ["/$DEV"])
@ConditionalOnNotProd
class DevController(val pdl : PersonTjeneste, val skjerming: SkjermingTjeneste, val ansatt: EntraTjeneste, val tjeneste: TilgangsTjeneste)
{
    @GetMapping(PDL)
    fun hentPerson(fnr: Fødselsnummer) = pdl.kandidat(fnr)

    @GetMapping("$PDL/gt")
    fun gt(fnr: Fødselsnummer) = pdl.gt(fnr)

    @GetMapping(SKJERMING)
    fun erSkjermet(fnr: Fødselsnummer) = skjerming.erSkjermet(fnr)

    @GetMapping("ansatt")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)

    @GetMapping("ansattilganger")
    fun hentAnsattTilganger(azureId : UUID) = ansatt.ansattTilganger(azureId)

    @GetMapping("tilgang")
    fun sjekkTilgang(@RequestParam saksbehandler: NavId,@RequestParam  kandidat: Fødselsnummer) = tjeneste.harTilgang(saksbehandler, kandidat)

}
