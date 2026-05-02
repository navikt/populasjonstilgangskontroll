package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component

@Component
class SkjermingPingable(client: SkjermingClient, cfg: SkjermingConfig) : AbstractPingable(cfg, client::ping)
