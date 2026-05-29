package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.RetryListener

class KafkaDroppedMessageMeter(private val registry: MeterRegistry) : RetryListener {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun recovered(record: ConsumerRecord<*, *>, ex: Exception?) {
        registry.counter("kafka.message.dropped",
            "topic", record.topic(),
            "exception", ex?.javaClass?.simpleName ?: "unknown").increment()
        log.error("Ga opp Kafka-melding på topic=${record.topic()} partition=${record.partition()} offset=${record.offset()}: ${ex?.message}", ex)
    }

    override fun failedDelivery(record: ConsumerRecord<*, *>, ex: Exception?, deliveryAttempt: Int) {
        log.warn("Forsøk $deliveryAttempt feilet for melding på topic=${record.topic()} offset=${record.offset()}: ${ex?.message}")
    }
}