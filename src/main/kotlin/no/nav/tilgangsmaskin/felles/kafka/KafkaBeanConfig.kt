package no.nav.tilgangsmaskin.felles.kafka

import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.util.backoff.ExponentialBackOff

@Configuration
@NoCoverageAnalysis
class KafkaBeanConfig {

    @Bean
    fun commonErrorHandler(listeners: List<KafkaTypedDroppedMessageMeter<*>>) =
        createErrorHandler( *listeners.toTypedArray())

    companion object {
        private fun createErrorHandler( vararg listeners: RetryListener) =
            DefaultErrorHandler(ExponentialBackOff(1_000L, 2.0).apply {
                    maxInterval = 30_000L
                    maxElapsedTime = 60_000L
                }
            ).apply {
                setRetryListeners(*listeners)
            }
    }
}
