package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidResolver
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverableService
@Timed(value = "entra", histogram = true)
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidResolver) {

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), true)

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), false)

    private fun resolve(ansattId: AnsattId) = resolver.oidForAnsatt(ansattId).toString()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter resolver=$resolver]"
}
