package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@RetryingWhenRecoverable
@Service
class EntraProxyTjeneste(private val adapter: EntraProxyRestClientAdapter)  {

    @WithSpan
    fun enhet(ansattId: AnsattId) = adapter.enhetForAnsatt(ansattId.verdi)

    fun enheter(ansattId: AnsattId) = adapter.enheterForAnsatt(ansattId.verdi)


    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}


