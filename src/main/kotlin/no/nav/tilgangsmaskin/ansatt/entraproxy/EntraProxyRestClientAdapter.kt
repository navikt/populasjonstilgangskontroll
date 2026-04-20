package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.springframework.stereotype.Component

@Component
class EntraProxyRestClientAdapter(private val client: EntraProxyClient, private val cfg: EntraProxyConfig) : Pingable {

    override val name = cfg.name
    override val pingEndpoint = "${cfg.pingEndpoint}"
    override val isEnabled = cfg.isEnabled
    override fun ping() = if (cfg.isEnabled) client.ping() else "disabled"

    fun enhetForAnsatt(ansattId: String) = client.ansatt(ansattId).enhet

    fun enheterForAnsatt(ansattId: String) = client.enheter(ansattId)
}