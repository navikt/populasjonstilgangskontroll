package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidResolver
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.retry.annotation.Retryable

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class Entra(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidResolver) {


    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), true)

    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), false)

    private fun resolve(ansattId: AnsattId) = resolver.oidForAnsatt(ansattId).toString()

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
