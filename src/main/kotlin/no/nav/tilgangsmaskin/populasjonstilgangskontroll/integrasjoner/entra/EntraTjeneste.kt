package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.FellesRetryListener
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor
import org.slf4j.LoggerFactory
import java.util.UUID

@RetryingOnRecoverableCacheableService(cacheNames = [GRAPH])
class EntraTjeneste(private val adapter: EntraClientAdapter, private val accessor: TokenAccessor) {

    private val log = LoggerFactory.getLogger(EntraTjeneste::class.java)

    fun ansatt(ident: AnsattId) =
        run {
            oid()?.let {
                log.info("Henter gruppemedlemsskap via oid fra token '$it'")
                EntraResponse(adapter.grupper("$it"))
            } ?: run {
                log.info("Henter gruppemedlemsskap via navident '$ident'")
                val attributter = adapter.attributter(ident.verdi).attributter.first()
                val grupper = adapter.grupper(attributter.id.toString())
                EntraResponse(grupper, attributter.tilAttributter())
            }
        }
    private fun oid() = runCatching {
        accessor.identFromToken
    }.getOrNull()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}



private fun EntraSaksbehandlerResponse.MSGraphSaksbehandlerAttributter.tilAttributter() = EntraResponsMapper.mapAttributter(this)

data class EntraResponse(val grupper: List<EntraGruppe>,val attributter: Ansatt.AnsattAttributter? = null)

