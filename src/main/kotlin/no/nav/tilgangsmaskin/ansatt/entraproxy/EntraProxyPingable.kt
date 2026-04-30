package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component

@Component
class EntraProxyPingable(client: EntraProxyClient, cfg: EntraProxyConfig) : AbstractPingable(cfg, client::ping)
