package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class OIDEndringTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "endringer", "Endrede oids")

