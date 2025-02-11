package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler.SaksbehandlerAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraSaksbehandlerResponse.MSGraphSaksbehandlerAttributter

object EntraResponsMapper {
        fun mapAttributter(respons: MSGraphSaksbehandlerAttributter)=
            with(respons) {
              SaksbehandlerAttributter(id, onPremisesSamAccountName, Navn(givenName, surname), streetAddress)
            }
       }