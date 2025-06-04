package no.nav.tilgangsmaskin.felles.rest.cache

import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.stereotype.Component

@Component
class ValKeyHealthIndicator(adapter: ValKeyAdapter) : PingableHealthIndicator(adapter)