package no.nav.tilgangsmaskin.ansatt.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.IrrecoverableRestException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class EntraRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, val cf: EntraConfig) :
    AbstractRestClientAdapter(restClient, cf) {

    fun oidFraEntra(ansattId: String) = get<EntraSaksbehandlerRespons>(cf.userURI(ansattId)).oids.let {
        if (it.isEmpty()) {
            throw IrrecoverableRestException(
                    NOT_FOUND, cf.userURI(ansattId), "Fant ingen data for ansatt $ansattId")
        }
        it.first().id
    }

    fun grupper(ansattId: String) =
        generateSequence(get<EntraGrupperBolk>(cf.grupperURI(ansattId))) { bolk ->
            bolk.next?.let {
                get<EntraGrupperBolk>(it)
            }
        }.flatMap {
            it.value
        }.toList().map {
            EntraGruppe(it.id, it.displayName)
        }.onEach {
            if (it.displayName == "N/A") log.error("Fant ikke displayName for gruppe ${it.id} for $ansattId")
        }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraSaksbehandlerRespons(@JsonProperty("value") val oids: List<MSGraphSaksbehandlerOids>) {
        data class MSGraphSaksbehandlerOids(val id: UUID)
    }

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

}