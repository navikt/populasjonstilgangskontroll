package no.nav.tilgangsmaskin.ansatt.entra

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: EntraOidResolver) {

    fun ansatt(ansattId: AnsattId) =
        resolver.oidForAnsatt(ansattId).let {
            EntraResponse(it, adapter.grupper("$it"))
        }

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
