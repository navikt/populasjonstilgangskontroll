package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.kafka.TypedKafkaDroppedMessageMeter
import org.springframework.stereotype.Component

@Component
class OppfølgingDroppedMessageMeter(registry: MeterRegistry) :
    TypedKafkaDroppedMessageMeter<OppfølgingHendelse>(registry, OppfølgingHendelse::class) {

}
