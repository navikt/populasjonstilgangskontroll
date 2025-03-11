package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraSaksbehandlerResponse.MSGraphSaksbehandlerAttributter

object EntraResponsMapper {
        fun mapAttributter(respons: MSGraphSaksbehandlerAttributter, ident: AnsattId)=
            with(respons) {
              AnsattAttributter(id, ident)
            }
       }