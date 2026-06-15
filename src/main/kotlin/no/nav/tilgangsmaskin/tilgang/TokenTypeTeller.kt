package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import org.springframework.stereotype.Component

@Component
class TokenTypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "token.type", "Token type")

