package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.RetryListener
import kotlin.reflect.KClass


abstract class KafkaTypedDroppedMessageMeter<T : Any>(
    registry: MeterRegistry,
    private val eventType: KClass<T>) : RetryListener {

    private val log = LoggerFactory.getLogger(javaClass)
    private val counter = KafkaDroppedMessageCounter(registry)

    protected open fun formatEvent(event: T) = "$event"

    override fun recovered(record: ConsumerRecord<*, *>, e: Exception?) {
        counter.increment(record, e)
        val event = typedValue(record)?.let { formatEvent(it) } ?: "<ikke-deserialiserbar verdi>"
        log.error("Ga opp Kafka-melding på topic=${record.topic()} partition=${record.partition()} offset=${record.offset()} hendelse=[$event]: ${e?.message}", e)
    }

    override fun failedDelivery(record: ConsumerRecord<*, *>, e: Exception?, n: Int) {
        log.warn("Forsøk $n feilet for melding på topic=${record.topic()} offset=${record.offset()}: ${e?.message}")
    }

    private fun typedValue(record: ConsumerRecord<*, *>): T? =
        record.value()?.let { value ->
            if (eventType.isInstance(value)) eventType.java.cast(value) else null
        }
}