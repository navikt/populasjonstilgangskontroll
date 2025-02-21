package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class  EntraClientAdapter(@Qualifier(GRAPH) restClient: RestClient,
                          private val cf: EntraConfig,
                          errorHandler: ErrorHandler): AbstractRestClientAdapter(restClient,cf, errorHandler) {

    fun attributter(ansattId: String) = get<EntraSaksbehandlerResponse>(cf.userURI(ansattId)).tilAttributter()

    fun grupper(ansattId: String) =
        generateSequence(get<EntraGrupperBolk>(cf.grupperURI(ansattId))) {
            it.next?.let {
                get<EntraGrupperBolk>(it)
            }
        }.flatMap {
            it.value
        }.toList().toTypedArray()

    private fun EntraSaksbehandlerResponse.tilAttributter() = EntraResponsMapper.mapAttributter(attributter.first())

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

}