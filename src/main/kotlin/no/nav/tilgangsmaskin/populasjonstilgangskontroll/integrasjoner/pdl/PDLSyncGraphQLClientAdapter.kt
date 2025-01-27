package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractGraphQLAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Cluster.Companion.isProd
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class PDLGraphQLClientAdapter(@Qualifier(PDLConfig.Companion.PDL) private val graphQlClient: GraphQlClient,
                              @Qualifier(PDLConfig.Companion.PDL) restClient: RestClient,
                              private val handler: ErrorHandler,
                              cfg: PDLConfig) : AbstractGraphQLAdapter(restClient, cfg) {

    override fun ping(): Map<String, String> {
        restClient
            .options()
            .uri(baseUri)
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus({ it.isError }) { req, res -> handler.handle(req, res) }
            .toBodilessEntity()
        return emptyMap()
    }

    fun person(ident: String) = query<Person>(graphQlClient, PERSON_QUERY, mapOf(IDENT to ident))

    override fun toString() =
        "${javaClass.simpleName} [restClient=$restClient,graphQlClient=$graphQlClient, cfg=$cfg]"

    companion object {
        private val IDENT = "ident"
        private val PERSON_QUERY = "query-pe!gc" +
                "rson" to "hentPerson"
    }
}

@JvmInline
value class Fødselsnummer(@get:JsonValue val verdi: String) {

    enum class Type { DNR, FNR, TENOR }

    fun type() = when (verdi[0]) {
        '8', '9' -> Type.TENOR
        '4', '5' -> Type.DNR
        else -> Type.FNR
    }

    init {
        require(verdi.length == 11) { "Fødselsnummer $verdi er ikke 11 siffer" }
        if (isProd) {
            require(mod11(W1, verdi) == verdi[9] - '0') { "Første kontrollsiffer $verdi[9] ikke validert" }
            require(mod11(W2, verdi) == verdi[10] - '0') { "Andre kontrollsiffer $verdi[10] ikke validert" }
        }
    }

    companion object {

        private fun String.partialMask(mask: Char = '*'): String {
            val start = length.div(2)
            return replaceRange(start + 1, length, mask.toString().repeat(length - start - 1))
        }

        private val W1 = intArrayOf(2, 5, 4, 9, 8, 1, 6, 7, 3)
        private val W2 = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5)

        private fun mod11(weights: IntArray, fnr: String) =
            with(weights.indices.sumOf { weights[it] * (fnr[(weights.size - 1 - it)] - '0') } % 11) {
                when (this) {
                    0 -> 0
                    1 -> throw IllegalArgumentException(fnr)
                    else -> 11 - this
                }
            }
    }

    override fun toString() = "${javaClass.simpleName} [fnr=${verdi.partialMask()}]"

}