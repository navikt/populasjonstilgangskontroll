package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.MSGraphSaksbehandlerResponse.MSGraphSaksbehandlerAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.Saksbehandler.SaksbehandlerAttributter

object EntraReeponsMapper {
        fun mapAttributter(respons: MSGraphSaksbehandlerAttributter)=
            with(respons) {
              SaksbehandlerAttributter(id, onPremisesSamAccountName, givenName, surname, streetAddress)
            }
       }