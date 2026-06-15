package no.nav.tilgangsmaskin.felles.kafka

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
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
 * Hver konsument har sin egen [KafkaTypedDroppedMessageMeter] for typesikker logging
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
    fun commonErrorHandler(listeners: List<KafkaTypedDroppedMessageMeter<*>>) =
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

