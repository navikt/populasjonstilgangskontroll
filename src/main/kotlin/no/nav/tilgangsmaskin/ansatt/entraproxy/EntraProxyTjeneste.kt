package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.rest.RestRetryingWhenRecoverableService
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import org.springframework.web.service.registry.ImportHttpServices

@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [EntraProxyClient::class], group = ENTRAPROXY)
class EntraProxyTjeneste(
    private val client: EntraProxyClient,
) {
    fun enhet(ansattId: AnsattId) = client.enhet(ansattId.verdi).enhet

    fun mail(message: MailRequest) = client.mail(message)

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}
