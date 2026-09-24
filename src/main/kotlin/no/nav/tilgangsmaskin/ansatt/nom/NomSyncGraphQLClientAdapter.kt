package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomGraphQLConfig.Companion.NOMGRAPH
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.pdl.Partnere
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLErrorHandler
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component

@Component
class NomSyncGraphQLClientAdapter(
    private val cfg: NomGraphQLConfig,
    @Qualifier(NOMGRAPH) private val client: GraphQlClient,
    private val errorHandler: NomGraphQLErrorHandler = NomGraphQLErrorHandler()) {

    private val log = getLogger(javaClass)

    fun leder(enhetsnummer: String): Any =
        runCatching {
            query<Any>(LEDER_QUERY, navIdent(enhetsnummer))
        }.getOrThrow()

    private inline fun <reified T : Any> query(query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            client
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java) ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR,
                cfg.baseUri,
                "Fant ikke feltet ${query.second} i responsen")
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            errorHandler.handle(cfg.baseUri, it)
        }

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [graphQlClient=$client, cfg=$cfg]"

    companion object {
        private const val ENHET = "orgenhetId"
        private fun navIdent(enhetsnummer: String) = mapOf(ENHET to enhetsnummer)
        private val LEDER_QUERY = "query-leder" to "orgEnhet"
    }
}
