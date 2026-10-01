package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.instrument.MeterRegistry
import no.nav.sikkerhetstjenesten.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.AbstractTeller
import org.springframework.stereotype.Component

@Component
class NasjonalGruppeTeller(registry: MeterRegistry, authContext: AuthContext) :
    AbstractTeller(registry, authContext, "gruppe.medlemskap.nasjonal", "Ansatte med og uten nasjonalt gruppemedlemsskap")
