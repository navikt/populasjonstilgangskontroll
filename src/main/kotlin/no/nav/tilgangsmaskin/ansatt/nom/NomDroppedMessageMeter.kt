package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.kafka.TypedKafkaDroppedMessageMeter
import org.springframework.stereotype.Component

@Component
class NomDroppedMessageMeter(registry: MeterRegistry) :
    TypedKafkaDroppedMessageMeter<NomHendelse>(registry, NomHendelse::class) {

    override fun formatEvent(event: NomHendelse) = "redacted"
}
