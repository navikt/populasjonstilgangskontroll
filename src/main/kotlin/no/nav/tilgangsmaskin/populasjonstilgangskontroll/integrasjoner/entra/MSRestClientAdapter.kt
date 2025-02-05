package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.MSGraphSaksbehandlerResponse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.client.RestClient
import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class  MSRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient,
                           private val cf: MSGraphConfig,
                           errorHandler: ErrorHandler): AbstractRestClientAdapter(restClient,cf, errorHandler) {

    fun attributterForIdent(ident: String) = get<MSGraphSaksbehandlerResponse>(cf.userURI(ident)).mapTo()

    fun grupperForUUID(ansattId: UUID) =
        generateSequence(get<EntraGrupperBolk>(cf.grupperURI(ansattId))) {
            it.next?.let {
                get<EntraGrupperBolk>(it)
            }
        }.flatMap {
            it.value
        }.toList()

    private fun MSGraphSaksbehandlerResponse.mapTo() = EntraReeponsMapper.mapAttributter(attributter.first())

}