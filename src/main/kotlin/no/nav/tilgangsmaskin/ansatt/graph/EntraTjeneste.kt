package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidResolver
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverableService
@Cacheable(cacheNames = [GRAPH])
@Timed(value = "entra", histogram = true)
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidResolver) {

    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), true)

    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), false)

    fun resolve(ansattId: AnsattId) = resolver.oidForAnsatt(ansattId).toString()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
