package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord

/**
 * Micrometer-counter for droppede Kafka-meldinger.
 * Tagger med topic, partisjon og exception-type for presis alerting og filtrering i Grafana.
 */
class KafkaDroppedMessageCounter(private val registry: MeterRegistry) {
    fun increment(record: ConsumerRecord<*, *>, e: Exception?) =
        registry.counter(
            "kafka.message.dropped",
            "topic", record.topic(),
            "partition", record.partition().toString(),
            "exception", e?.javaClass?.simpleName ?: "unknown"
        ).increment()
}