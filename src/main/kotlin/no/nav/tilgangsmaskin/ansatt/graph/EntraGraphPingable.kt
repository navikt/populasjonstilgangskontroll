package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component

@Component
class EntraGraphPingable(client: EntraGraphClient, cfg: EntraConfig) : AbstractPingable(cfg, client::ping)

