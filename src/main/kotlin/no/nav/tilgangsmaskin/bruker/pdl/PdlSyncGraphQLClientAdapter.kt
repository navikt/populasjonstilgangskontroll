package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.sikkerhetstjenesten.felles.domain.BrukerId
import no.nav.sikkerhetstjenesten.felles.graphql.AbstractSyncGraphQLClientAdapter
import no.nav.sikkerhetstjenesten.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.stereotype.Component

@Component
class PdlSyncGraphQLClientAdapter(
    cfg: PdlGraphQLConfig,
    @Qualifier(PDLGRAPH) client: GraphQlClient) : AbstractSyncGraphQLClientAdapter(cfg, client) {

    fun partnere(ident: String): Set<FamilieMedlem> =
        runCatching {
            queryRequired<Partnere>(SIVILSTAND_QUERY, ident(ident)).sivilstand.mapNotNullTo(mutableSetOf()) {
                it.relatertVedSivilstand?.let { brukerId ->
                    FamilieMedlem(BrukerId(brukerId), tilPartner(it.type))
                }
            }
        }.recover { e ->
            (e as? NotFoundRestException)?.let {
                log.trace("Fant ingen partnere for $ident")
                emptySet()
            } ?: throw e
        }.getOrThrow()

    companion object {
        private const val IDENT = "ident"
        private fun ident(ident: String) = mapOf(IDENT to ident)
        private val SIVILSTAND_QUERY = "query-sivilstand" to "hentPerson"
    }
}
