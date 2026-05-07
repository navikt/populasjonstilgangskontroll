package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component

@Component
class PdlGraphQLPingable(client: PdlGraphQLPingClient, cfg: PdlGraphQLConfig) : AbstractPingable(cfg, client::ping)

