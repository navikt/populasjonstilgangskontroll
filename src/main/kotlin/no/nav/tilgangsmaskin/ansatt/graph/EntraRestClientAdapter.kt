package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArray
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import org.springframework.web.ErrorResponseException
import org.springframework.web.client.RestClient
import java.util.*

@Component
class EntraRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, val cf: EntraConfig) : AbstractRestClientAdapter(restClient, cf) {

    fun oidFraEntra(ansattId: String) =
         with(get<EntraSaksbehandlerRespons>(cf.userURI(ansattId)).oids) {
             log.info("Fant $size oids i Entra for $ansattId")
            when (size) {
                0 -> throw OidException(ansattId, "Fant ingen oid for navident $ansattId, er den fremdeles gyldig?")
                1 -> single().id
                else -> throw OidException(ansattId, "Forventet nøyaktig én oid for navident $ansattId, fant $size (${joinToString(", ") { it.id.toString() }})")
            }
    }

    fun grupper(ansattId: String, trengerGlobaleGrupper: Boolean): Set<EntraGruppe> =
        generateSequence(get<EntraGrupper>(cf.grupperURI(ansattId,trengerGlobaleGrupper))) { bolk ->
            bolk.next?.let {
                get<EntraGrupper>(it)
            }
        }
            .flatMap { it.value }
            .toSet()


    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraSaksbehandlerRespons(@param:JsonProperty("value") val oids: Set<EntraOids>) {
        data class EntraOids(val id: UUID)
    }

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

    class OidException(ansattId: String, msg: String) : ErrorResponseException(NOT_FOUND) {
        init {
            body.title = TITLE
            body.detail = msg
            body.properties = mapOf("navident" to ansattId)
        }

        companion object   {
            const val TITLE = "Uventet respons fra Entra"
        }
    }
}