package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component

@Component
class VergemålPingable(client: VergemålClient, cfg: VergemålConfig) : AbstractPingable(cfg, client::ping)
