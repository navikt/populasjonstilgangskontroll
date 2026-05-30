package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.stereotype.Component
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
 * Spring Boot plukker commonErrorHandler-bønnen opp automatisk for autokonfigurert
 * ConcurrentKafkaListenerContainerFactory. Egne factories må injisere den eksplisitt.
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
    fun commonErrorHandler(listener: KafkaDroppedMessageMeter) =
        DefaultErrorHandler(
            ExponentialBackOff(1_000L, 2.0).apply {
                maxInterval = 30_000L
                maxElapsedTime = 60_000L
            }
        ).apply {
            setRetryListeners(listener)
        }
}

@Component
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