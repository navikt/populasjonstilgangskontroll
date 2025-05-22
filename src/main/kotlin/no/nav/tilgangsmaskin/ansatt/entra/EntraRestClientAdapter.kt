package no.nav.tilgangsmaskin.ansatt.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.net.URI

@Component
class EntraRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, val cf: EntraConfig) :
    AbstractRestClientAdapter(restClient, cf) {


    fun oidFraEntra(ansattId: String) =
        get<EntraSaksbehandlerRespons>(cf.userURI(ansattId)).oids.single().id

    fun grupper(ansattId: String, trengerGlobaleGrupper: Boolean): Set<EntraGruppe> =
        generateSequence(get<EntraGrupper>(cf.grupperURI(ansattId,trengerGlobaleGrupper))) { bolk ->
            bolk.next?.let {
                get<EntraGrupper>(it)
            }
        }
            .flatMap { it.value }
            .map { EntraGruppe(it.id, it.displayName) }
            .toSet()


    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraSaksbehandlerRespons(@JsonProperty("value") val oids: Set<EntraOids>) {
        data class EntraOids(val id: UUID)
    }

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

}