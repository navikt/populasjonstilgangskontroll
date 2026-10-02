package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.sikkerhetstjenesten.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.AbstractTeller
import org.springframework.stereotype.Component

@Component
class EvalueringTypeTeller(registry: MeterRegistry, authContext: AuthContext) :
    AbstractTeller(registry, authContext, "evalueringtype.resultat", "Evalueringsresultat pr type og begrunnelse")
