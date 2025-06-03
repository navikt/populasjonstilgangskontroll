package no.nav.tilgangsmaskin.felles.rest.cache

import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.stereotype.Component

@Component
class ValkeyHealthIndicator(config: ValkeyBeanConfiguration) : PingableHealthIndicator(config)