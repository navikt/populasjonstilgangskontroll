package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.springframework.web.service.registry.ImportHttpServices

@RetryingWhenRecoverableRestService
@ImportHttpServices(types = [EntraProxyClient::class], group = ENTRAPROXY)
class EntraProxyTjeneste(private val client: EntraProxyClient) {

    @WithSpan
    fun enhet(ansattId: AnsattId) =
        client.enhet(ansattId.verdi).enhet

    @WithSpan
    fun enheter(ansattId: AnsattId) =
        client.enheter(ansattId.verdi)

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}


