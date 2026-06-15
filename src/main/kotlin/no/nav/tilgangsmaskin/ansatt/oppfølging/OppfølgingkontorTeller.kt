package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class OppfølgingkontorTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "oppfolging", "Oppfølgingkontor treff/bom")

