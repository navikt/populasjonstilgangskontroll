package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.bruker.pdl.generated.client.HentPersonGraphQLQuery
import no.nav.tilgangsmaskin.bruker.pdl.generated.client.HentPersonProjectionRoot
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.graphql.client.DgsGraphQlClient
import org.springframework.graphql.client.toEntityList
import org.springframework.stereotype.Component
import  no.nav.tilgangsmaskin.bruker.pdl.generated.DgsConstants.QUERY.HentPerson
import  no.nav.tilgangsmaskin.bruker.pdl.generated.DgsConstants.PERSON.Sivilstand

private const val SIVILSTAND_PATH = "$HentPerson.$Sivilstand"

@Component
class PdlDgsGraphQLClientAdapter(
    private val cfg: PdlGraphQLConfig,
    private val dgsClient: DgsGraphQlClient,
    private val errorHandler: PdlGraphQLErrorHandler = PdlGraphQLErrorHandler()
) {
    private val log = getLogger(javaClass)

    fun partnere(ident: String): Set<FamilieMedlem> =
        runCatching {
            hentSivilstand(ident).mapNotNullTo(mutableSetOf()) { sivilstand ->
                sivilstand.type?.let { type ->
                    sivilstand.relatertVedSivilstand?.let { relatertIdent ->
                        FamilieMedlem(BrukerId(relatertIdent), tilPartner(type.toDomain()))
                    }
                }
            }
        }.recover { e ->
            (e as? NotFoundRestException)?.let {
                log.trace("Fant ingen partnere for $ident")
                emptySet()
            } ?: throw e
        }.getOrThrow()

    private fun hentSivilstand(ident: String): List<DgsSivilstand> =
        runCatching {
            dgsClient
                .request(HentPersonGraphQLQuery.newRequest().ident(ident).build())
                .projection(
                    HentPersonProjectionRoot<Nothing, Nothing>()
                        .sivilstand()
                        .relatertVedSivilstand()
                        .type()
                )
                .retrieveSync(SIVILSTAND_PATH)
                .toEntityList<DgsSivilstand>()
        }.getOrElse { e ->
            log.warn("Feil ved henting av sivilstand", e)
            errorHandler.handle(cfg.baseUri, e)
        }

    private data class DgsSivilstand(
        val type: String? = null,
        val relatertVedSivilstand: String? = null,
    )

    private fun String.toDomain() =
        Partnere.Sivilstand.Sivilstandstype.valueOf(this)

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [dgsClient=$dgsClient, cfg=$cfg]"
}
