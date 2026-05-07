package no.nav.tilgangsmaskin.bruker.pdl
import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component
@Component
class PdlPingable(client: PdlPipClient, cfg: PdlConfig) : AbstractPingable(cfg, client::ping)
