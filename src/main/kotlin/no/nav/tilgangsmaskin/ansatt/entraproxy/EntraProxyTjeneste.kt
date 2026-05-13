package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService

@RetryingWhenRecoverableRestService
class EntraProxyTjeneste(private val client: EntraProxyClient)  {

    @WithSpan
    fun enhet(ansattId: AnsattId) =
        client.ansatt(ansattId.verdi).enhet
    @WithSpan
    fun enheter(ansattId: AnsattId) =
        client.enheter(ansattId.verdi)

    @Generated
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}


