package no.nav.tilgangsmaskin.felles.kafka

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.ExponentialBackOff

/**
 * Felles Kafka-konfigurasjon: feilhåndtering.
 *
 * Deserializer-oppsett (ErrorHandlingDeserializer med JacksonJsonDeserializer) konfigureres
 * via application-gcp.yaml properties, ikke via bean.
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
    fun commonErrorHandler(listeners: List<KafkaTypedDroppedMessageMeter<*>>) =
        DefaultErrorHandler(
            ExponentialBackOff(1_000L, 2.0).apply {
                this.maxInterval = 30_000L
                this.maxElapsedTime = 60_000L
            }
        ).apply {
            setRetryListeners(*listeners.toTypedArray())
        }

}

