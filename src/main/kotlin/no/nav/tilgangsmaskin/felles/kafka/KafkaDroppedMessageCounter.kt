package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import no.nav.sikkerhetstjenesten.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import org.apache.kafka.clients.consumer.ConsumerRecord

class KafkaDroppedMessageCounter(private val registry: MeterRegistry) {
    fun increment(record: ConsumerRecord<*, *>, e: Exception?) =
        registry.counter(
            "kafka.message.dropped",
            "topic", record.topic(),
            "partition", record.partition().toString(),
            "exception", e?.javaClass?.simpleName ?: UTILGJENGELIG
        ).increment()
}

