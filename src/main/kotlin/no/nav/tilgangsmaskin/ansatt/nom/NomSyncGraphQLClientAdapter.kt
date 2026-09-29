package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomGraphQLConfig.Companion.NOMGRAPH
import no.nav.tilgangsmaskin.felles.graphql.AbstractSyncGraphQLClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.stereotype.Component

@Component
class NomSyncGraphQLClientAdapter(
    cfg: NomGraphQLConfig,
    @Qualifier(NOMGRAPH) client: GraphQlClient) : AbstractSyncGraphQLClientAdapter(cfg, client) {

    fun lederForAnsatt(ansattId: String) = query<NomRessurs>(LEDER_QUERY, ident(ansattId))

    companion object {
        private const val IDENT = "navident"
        private fun ident(navident: String) = mapOf(IDENT to navident)
        private val LEDER_QUERY = "query-leder" to "ressurs"
    }
}
