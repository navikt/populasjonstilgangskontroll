package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsTjeneste

@ProtectedRestController(value = ["/api/v1"], issuer = "azuread", claimMap = [])
class Tilgangskontroll(val service : TilgangsTjeneste) {


}

