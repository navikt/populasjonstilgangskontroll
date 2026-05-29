package no.nav.tilgangsmaskin.felles.kafka

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.util.backoff.ExponentialBackOff

/**
 * Felles Kafka-feilhåndtering for alle @KafkaListener-konsumenter.
 *
 * Erstatter Spring sin default (9 umiddelbare retries) med eksponensiell backoff:
 * 1s → 2s → 4s → 8s → 16s → 30s, opp til 1 min totalt.
 *
 * Når retries er gitt opp, inkrementeres metrikken `kafka.message.dropped`
 * og hendelsen logges på ERROR-nivå.
 *
 * Spring Boot plukker denne bønnen opp automatisk for autokonfigurert
 * ConcurrentKafkaListenerContainerFactory. Egne factories må injisere
 * den eksplisitt.
 */
@Configuration
class KafkaFellesConfig {

    @Bean
    fun droppedMessageMeter(meterRegistry: MeterRegistry) =
        KafkaDroppedMessageMeter(meterRegistry)

    @Bean
    fun commonErrorHandler(listener: RetryListener) =
        DefaultErrorHandler(
            ExponentialBackOff(1_000L, 2.0).apply {
                maxInterval = 30_000L
                maxElapsedTime = 60_000L
            }
        ).apply {
            setRetryListeners(listener)
        }
}

