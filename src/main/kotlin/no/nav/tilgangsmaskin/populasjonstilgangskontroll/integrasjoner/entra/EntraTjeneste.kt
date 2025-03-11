package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.slf4j.LoggerFactory
import java.util.*

@RetryingOnRecoverableCacheableService(cacheNames = [GRAPH])
class EntraTjeneste(private val adapter: EntraClientAdapter, private val accessor: TokenClaimsAccessor) {

    private val log = LoggerFactory.getLogger(EntraTjeneste::class.java)

    fun ansatt(ident: AnsattId) =
        run {
            oid()?.let {
                log.trace("Henter gruppemedlemsskap via oid '{}' fra token", it)
                EntraResponse(it, adapter.grupper("$it"))
            } ?: run {
                log.info("Henter gruppemedlemsskap via navident '$ident'")
                val oid = adapter.idForIdent(ident.verdi).oids.single().id.also {
                    log.info("oid er {}", it)
                }
                val grupper = adapter.grupper("$oid").also {
                    log.info("Grupper er {}", it)
                }
                EntraResponse(oid, grupper)
            }
        }

    private fun oid() = runCatching { accessor.identFromToken }.getOrNull()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}

data class EntraResponse(val oid: UUID, val grupper: List<EntraGruppe>)

