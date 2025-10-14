package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@RetryingWhenRecoverable
@Service
@Timed(value = "entra", histogram = true)
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidTjeneste)  {

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    @WithSpan
    fun geoOgGlobaleGrupper(ansattId: AnsattId, oid: UUID) = adapter.grupper(oid.toString(), true)

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    @WithSpan
    fun geoGrupper(ansattId: AnsattId, oid: UUID) = adapter.grupper(oid.toString(), false)

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter resolver=$resolver]"
}


