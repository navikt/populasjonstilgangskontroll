package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomGraphQLConfig.Companion.NOMGRAPH
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.stereotype.Component

@Component
class NomSyncGraphQLClientAdapter(
    private val cfg: NomGraphQLConfig,
    @Qualifier(NOMGRAPH) private val client: GraphQlClient,
    private val errorHandler: NomGraphQLErrorHandler = NomGraphQLErrorHandler()) {

    private val log = getLogger(javaClass)

    fun lederForAnsatt(ansattId: String): NomRessurs? =
        runCatching {
            query<NomRessurs>(LEDER_QUERY, ident(ansattId))
        }.getOrThrow()

    private inline fun <reified T : Any> query(query: Pair<String, String>, vars: Map<String, String>): T? =
        runCatching {
            client
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java)
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            errorHandler.handle(cfg.baseUri, it)
        }

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [graphQlClient=$client, cfg=$cfg]"

    companion object {
        private const val IDENT = "navident"
        private fun ident(navident: String) = mapOf(IDENT to navident)
        private val LEDER_QUERY = "query-leder" to "ressurs"
    }
}
