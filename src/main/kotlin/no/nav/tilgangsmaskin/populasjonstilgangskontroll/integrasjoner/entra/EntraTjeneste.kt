package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class EntraTjeneste(private val adapter: EntraClientAdapter,private val resolver: OIDResolver ) {

    fun ansatt(ansattId: AnsattId) =
        resolver.oidForAnsatt(ansattId).let {
            EntraResponse(it, adapter.grupper("$it"))
        }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
