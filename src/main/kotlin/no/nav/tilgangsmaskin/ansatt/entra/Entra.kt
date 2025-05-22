package no.nav.tilgangsmaskin.ansatt.entra

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class Entra(private val adapter: EntraRestClientAdapter, private val resolver: EntraOidResolver) {

    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), true)

    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), false)

    private fun resolve(ansattId: AnsattId) = resolver.oidForAnsatt(ansattId).toString()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
