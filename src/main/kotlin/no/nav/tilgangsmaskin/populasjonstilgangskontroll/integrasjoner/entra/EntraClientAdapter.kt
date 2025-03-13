package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.util.*

@Component
class  EntraClientAdapter(@Qualifier(GRAPH) restClient: RestClient,
                          private val cf: EntraConfig,
                          errorHandler: ErrorHandler): AbstractRestClientAdapter(restClient,cf, errorHandler) {

    fun oidFraEntra(ansattId: String) = get<EntraSaksbehandlerRespons>(cf.userURI(ansattId)).oids.single().id

    fun grupper(ansattId: String) =
        generateSequence(get<EntraGrupperBolk>(cf.grupperURI(ansattId))) { bolk ->
            bolk.next?.let {
                get<EntraGrupperBolk>(it)
            }
        }.flatMap {
            it.value
        }.toList()

    fun grupperRaw(ansattId: String) =
        generateSequence(get<EntraGrupperBolkAny>(cf.grupperURI(ansattId))) { bolk ->
            bolk.next?.let {
                get<EntraGrupperBolkAny>(it)
            }
        }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraSaksbehandlerRespons(@JsonProperty("value") val oids: List<MSGraphSaksbehandlerOids>)  {
        data class MSGraphSaksbehandlerOids(val id: UUID)
    }

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

}