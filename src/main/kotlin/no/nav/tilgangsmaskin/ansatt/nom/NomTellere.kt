package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.regler.motor.AbstractEnkelTeller
import org.springframework.stereotype.Component

@Component
class NomKallTeller(registry: MeterRegistry) :
    AbstractEnkelTeller(registry, "vaktmester.kall", "Antall ganger kalt")

@Component
class NomRaderFjernetTeller(registry: MeterRegistry) :
    AbstractEnkelTeller(registry, "vaktmester.rader.fjernet", "Antall rader fjernet")

