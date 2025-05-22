package no.nav.tilgangsmaskin.ansatt.entra

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class Entra(private val adapter: EntraRestClientAdapter, private val resolver: EntraOidResolver) {

    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(ansattId.toString(), true)

    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolver.oidForAnsatt(ansattId).toString(), false)

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
