package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.slf4j.LoggerFactory

@RetryingOnRecoverableCacheableService(cacheNames = [GRAPH])
class EntraTjeneste(private val adapter: EntraClientAdapter, private val accessor: TokenClaimsAccessor) {

    private val log = LoggerFactory.getLogger(EntraTjeneste::class.java)

    fun ansatt(ident: AnsattId) =
        run {
            oid()?.let {
                log.trace("Henter gruppemedlemsskap via oid '{}' fra token", it)
                EntraResponse(adapter.grupper("$it"))
            } ?: run {
                log.info("Henter gruppemedlemsskap via navident '$ident'")
                val attributter = adapter.attributter(ident.verdi).attributter.first().also {
                    log.info("Attributter er {}", it)
                }
                val grupper = adapter.grupper(attributter.id.toString()).also {
                    log.info("Grupper er {}", it)
                }
                EntraResponse(grupper, attributter.tilAttributter(ident)).also {
                    log.info("Respons er $it")
                }
            }
        }

    private fun oid() = runCatching {
        accessor.identFromToken
    }.getOrNull()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}



private fun EntraSaksbehandlerResponse.MSGraphSaksbehandlerAttributter.tilAttributter(ident: AnsattId) = EntraResponsMapper.mapAttributter(this, ident)

data class EntraResponse(val grupper: List<EntraGruppe>,val attributter: Ansatt.AnsattAttributter? = null)

