package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.felles.security.AuthContext
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangTeller(registry: MeterRegistry, authContext: AuthContext) :
    AbstractTeller(registry, authContext, "overstyring.forsøk", "Enkelttilgang forsøk pr resultat")
