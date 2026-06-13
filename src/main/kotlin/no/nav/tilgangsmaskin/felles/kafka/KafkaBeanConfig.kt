package no.nav.tilgangsmaskin.felles.kafka

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.DeserializationException
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.messaging.converter.MessageConversionException
import org.springframework.util.backoff.BackOff
import org.springframework.util.backoff.ExponentialBackOff
import tools.jackson.databind.json.JsonMapper


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
    fun kafkaBackOff() =
        ExponentialBackOff(1_000L, 2.0).apply {
            maxInterval = 30_000L
            maxElapsedTime = 60_000L
        }

    @Bean
    fun commonErrorHandler(backOff: BackOff, vararg listeners: KafkaTypedDroppedMessageMeter<*>) =
        DefaultErrorHandler(backOff).apply {
            setRetryListeners(*listeners)
            addNotRetryableExceptions(
                DeserializationException::class.java,
                MessageConversionException::class.java,
            )
        }
}