package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.kafka.KafkaTypedDroppedMessageMeter
import org.springframework.stereotype.Component

@Component
class NomDroppedMessageMeter(registry: MeterRegistry) :
    KafkaTypedDroppedMessageMeter<NomHendelse>(registry, NomHendelse::class)
