package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidResolver
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict

@CacheableRetryingOnRecoverableService(cacheNames = [GRAPH])
@Timed
class Entra(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidResolver) {

    private val log = getLogger(javaClass)


    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), true)

    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), false)

    private fun resolve(ansattId: AnsattId) = resolver.oidForAnsatt(ansattId).toString()

    @Retryable(maxAttempts = 1)
    @CacheEvict(
        cacheNames = [GRAPH],
        key = "#root.targetClass.packageName + ':geoOgGlobaleGrupper:' + #ansattId")
    fun evict(ansattId: AnsattId) {
        log.info("Resetter cache for $ansattId")
    }

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter, resolver=$resolver]"
}
