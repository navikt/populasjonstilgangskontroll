package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: OidResolver ) {

    fun ansatt(ansattId: AnsattId) =
        resolver.oidForAnsatt(ansattId).let {
            EntraResponse(it, adapter.grupper("$it"))
        }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
