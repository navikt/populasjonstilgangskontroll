package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "overstyring.forsøk", "Enkelttilgang forsøk pr resultat")

