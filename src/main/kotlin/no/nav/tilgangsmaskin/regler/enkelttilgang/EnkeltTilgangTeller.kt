package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.instrument.MeterRegistry
import no.nav.sikkerhetstjenesten.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.AbstractTeller
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangTeller(registry: MeterRegistry, authContext: AuthContext) :
    AbstractTeller(registry, authContext, "overstyring.forsøk", "Enkelttilgang forsøk pr resultat")
