package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.MSGraphSaksbehandlerResponse.MSGraphSaksbehandlerAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler.SaksbehandlerAttributter

object EntraReeponsMapper {
        fun mapAttributter(respons: MSGraphSaksbehandlerAttributter)=
            with(respons) {
              SaksbehandlerAttributter(id, onPremisesSamAccountName, givenName, surname, streetAddress)
            }
       }