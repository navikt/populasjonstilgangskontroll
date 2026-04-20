package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableService

@RetryingWhenRecoverableService
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


