package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.util.backoff.ExponentialBackOff
import tools.jackson.databind.json.JsonMapper

/**
 * Felles Kafka-konfigurasjon: deserializer-oppsett og feilhåndtering.
 *
 * Feilhåndtering erstatter Spring sin default (9 umiddelbare retries) med eksponensiell backoff:
 * 1s → 2s → 4s → 8s → 16s → 30s, opp til 1 min totalt.
 *
 * Når retries er gitt opp, inkrementeres metrikken `kafka.message.dropped`
 * og hendelsen logges på ERROR-nivå.
 *
 * Hver konsument har sin egen [TypedKafkaDroppedMessageMeter] for typesikker logging
 * av hendelser som ikke kan prosesseres.
 */
@Configuration
@NoCoverageAnalysis
class KafkaBeanConfig {

    @Bean
    fun kafkaConsumerFactoryCustomizer(mapper: JsonMapper) =
        DefaultKafkaConsumerFactoryCustomizer {
            it.setValueDeserializerSupplier {
                ErrorHandlingDeserializer(JacksonJsonDeserializer(mapper))
            }
        }

    @Bean
    fun commonErrorHandler(listeners: List<TypedKafkaDroppedMessageMeter<*>>) =
        createErrorHandler(*listeners.toTypedArray())

    companion object {
        fun createErrorHandler(vararg listeners: RetryListener) =
            DefaultErrorHandler(
                ExponentialBackOff(1_000L, 2.0).apply {
                    maxInterval = 30_000L
                    maxElapsedTime = 60_000L
                }
            ).apply {
                setRetryListeners(*listeners)
            }
    }
}

/**
 * Typesikker [RetryListener] som logger droppede meldinger med kjent hendelsestype.
 *
 * Subklasser spesifiserer [eventType] og implementerer [formatEvent] for
 * domenespesifikk logging uten å eksponere sensitive data.
 */
abstract class TypedKafkaDroppedMessageMeter<T :Any>(
    private val registry: MeterRegistry,
    private val eventType: Class<T>) : RetryListener {

    private val log = getLogger(javaClass)

    /**
     * Formater hendelsen for logging. Implementasjoner bør maskere sensitive felt.
     */
    protected abstract fun formatEvent(event: T): String

    override fun recovered(record: ConsumerRecord<*, *>, ex: Exception?) {
        val event = typedValue(record) ?: return
        registry.counter(
            "kafka.message.dropped",
            "topic", record.topic(),
            "exception", ex?.javaClass?.simpleName ?: "unknown"
        ).increment()
        log.error(
            "Ga opp Kafka-melding på topic=${record.topic()} partition=${record.partition()} offset=${record.offset()} hendelse=[${formatEvent(event)}]: ${ex?.message}", ex)
    }

    override fun failedDelivery(record: ConsumerRecord<*, *>, ex: Exception?, deliveryAttempt: Int) {
        typedValue(record) ?: return
        log.warn(
            "Forsøk $deliveryAttempt feilet for melding på topic=${record.topic()} " +
                "offset=${record.offset()}: ${ex?.message}"
        )
    }

    private fun typedValue(record: ConsumerRecord<*, *>): T? =
        record.value()?.let { value ->
            if (eventType.isInstance(value)) eventType.cast(value) else null
        }
}