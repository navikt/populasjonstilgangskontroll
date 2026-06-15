package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class EvalueringTypeTeller(registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "evalueringtype.resultat", "Evalueringsresultat pr type og begrunnelse")

