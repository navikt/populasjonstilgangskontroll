package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import java.util.UUID

@RetryingOnRecoverableService
@Timed(value = "entra", histogram = true)
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidTjeneste) {

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    fun geoOgGlobaleGrupper(ansattId: AnsattId, oid: UUID) = adapter.grupper(oid.toString(), true)

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    fun geoGrupper(ansattId: AnsattId, oid: UUID) = adapter.grupper(oid.toString(), false)

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter resolver=$resolver]"
}
