package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.felles.security.AuthContext
import org.springframework.stereotype.Component

@Component
class EvalueringTypeTeller(registry: MeterRegistry, authContext: AuthContext) :
    AbstractTeller(registry, authContext, "evalueringtype.resultat", "Evalueringsresultat pr type og begrunnelse")
