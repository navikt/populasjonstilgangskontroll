package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraSaksbehandlerResponse.MSGraphSaksbehandlerAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler.SaksbehandlerAttributter

object EntraResponsMapper {
        fun mapAttributter(respons: MSGraphSaksbehandlerAttributter)=
            with(respons) {
              SaksbehandlerAttributter(id, onPremisesSamAccountName, givenName, surname, streetAddress)
            }
       }