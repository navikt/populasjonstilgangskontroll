package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.listener.RetryListener
import kotlin.reflect.KClass

abstract class KafkaTypedDroppedMessageMeter<T : Any>(
    registry: MeterRegistry,
    private val eventType: KClass<T>) : RetryListener {

    private val log = getLogger(javaClass)
    private val counter = KafkaDroppedMessageCounter(registry)

    protected open fun formatEvent(event: T) = "$event"

    override fun recovered(record: ConsumerRecord<*, *>, e: Exception?) {
        val event = typedValue(record) ?: return
        counter.increment(record, e)
        log.error("Ga opp Kafka-melding på topic=${record.topic()} partition=${record.partition()} offset=${record.offset()} hendelse=[${
            formatEvent(event)
        }]: ${e?.message}", e)
    }

    override fun failedDelivery(record: ConsumerRecord<*, *>, e: Exception?, deliveryAttempt: Int) {
        typedValue(record) ?: return
        log.warn("Forsøk $deliveryAttempt feilet for melding på topic=${record.topic()} offset=${record.offset()}: ${e?.message}"
        )
    }

    private fun typedValue(record: ConsumerRecord<*, *>): T? =
        record.value()?.let { value ->
            if (eventType.isInstance(value)) eventType.java.cast(value) else null
        }
}