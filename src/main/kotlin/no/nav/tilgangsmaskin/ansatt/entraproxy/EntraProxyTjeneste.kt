package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.springframework.web.service.registry.ImportHttpServices

@RetryingWhenRecoverableRestService
@ImportHttpServices(types = [EntraProxyClient::class], group = ENTRAPROXY)
class EntraProxyTjeneste(private val client: EntraProxyClient) {

    fun enhet(ansattId: AnsattId) =
        client.enhet(ansattId.verdi).enhet

    fun enheter(ansattId: AnsattId) =
        client.enheter(ansattId.verdi)

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}


