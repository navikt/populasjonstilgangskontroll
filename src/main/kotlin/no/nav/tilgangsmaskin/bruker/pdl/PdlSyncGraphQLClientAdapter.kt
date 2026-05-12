package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.graphql.PdlGraphQLErrorHandler
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component

@Component
@Timed(value = "pdl_tjeneste", histogram = true, extraTags = ["backend", "graphql"])
class PdlSyncGraphQLClientAdapter(
    private val cfg: PdlGraphQLConfig,
    private val client: GraphQlClient,
    private val errorHandler: PdlGraphQLErrorHandler = object : PdlGraphQLErrorHandler {},
    ) {
    private val log = getLogger(javaClass)

    fun partnere(ident: String): Set<FamilieMedlem> =
        runCatching {
            query<Partnere>(SIVILSTAND_QUERY, ident(ident)).sivilstand.mapNotNull {
                it.relatertVedSivilstand?.let { brukerId ->
                    FamilieMedlem(BrukerId(brukerId), tilPartner(it.type))
                }
            }.toSet()
        }.getOrElse {
            if (it is IrrecoverableRestException && it.statusCode == NOT_FOUND) {
                log.trace("Fant ingen partnere for $ident")
                return emptySet()
            } else throw it
        }

    private inline fun <reified T : Any> query(query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            client
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java) ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, cfg.baseUri, "Fant ikke feltet ${query.second} i responsen")
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            errorHandler.handle(cfg.baseUri, it)
        }

    @Generated
    override fun toString() =
        "${javaClass.simpleName} [graphQlClient=$client, cfg=$cfg]"

    companion object {
        private const val IDENT = "ident"
        private fun ident(ident: String) = mapOf(IDENT to ident)
        private val SIVILSTAND_QUERY = "query-sivilstand" to "hentPerson"
    }
}
