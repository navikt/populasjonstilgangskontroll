package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.opentelemetry.instrumentation.annotations.WithSpan
import javax.annotation.processing.Generated
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.springframework.stereotype.Service

@RetryingWhenRecoverable
@Service
class EntraProxyTjeneste(private val adapter: EntraProxyRestClientAdapter)  {

    @WithSpan
    fun enhet(ansattId: AnsattId) =
        adapter.enhetForAnsatt(ansattId.verdi)

    @WithSpan
    fun enheter(ansattId: AnsattId) =
        adapter.enheterForAnsatt(ansattId.verdi)

    @Generated
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}


