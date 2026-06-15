package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.kafka.TypedKafkaDroppedMessageMeter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@NoCoverageAnalysis
class OppfølgingBeanConfig {

    @Bean
    fun oppfølgingDroppedMessageMeter(registry: MeterRegistry) =
        object : TypedKafkaDroppedMessageMeter<OppfølgingHendelse>(registry, OppfølgingHendelse::class) {}
}
