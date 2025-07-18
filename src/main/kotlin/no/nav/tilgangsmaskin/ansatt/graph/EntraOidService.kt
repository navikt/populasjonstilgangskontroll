package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraOidService.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.rest.ConfigurableCache
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Duration

@Service
@Cacheable(cacheNames = [OID_CACHE])
class EntraOidService(private val adapter: EntraRestClientAdapter) : ConfigurableCache {
    fun oidForAnsatt(ansattId: AnsattId) = adapter.oidForAnsatt(ansattId.verdi)

    override val navn = OID_CACHE
    override val ttl  = Duration.ofDays(365)

    companion object   {
        const val OID_CACHE = "oid-cache"
    }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}