package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.ansatt.nom.NomHendelseKonsument.Companion.NOM_FNR_FILTER_STRATEGY
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.kafka.KafkaTypedDroppedMessageMeter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.adapter.RecordFilterStrategy

@Configuration
@NoCoverageAnalysis
class NomBeanConfig {

    @Bean(NOM_FNR_FILTER_STRATEGY)
    fun nomFnrFilterStrategy() =
        RecordFilterStrategy<String, NomHendelse> {
            runCatching {
                BrukerId(it.value().personident)
            }.isFailure
        }

    @Bean
    fun nomDroppedMessageMeter(registry: MeterRegistry) =
        object : KafkaTypedDroppedMessageMeter<NomHendelse>(registry, NomHendelse::class) {}
}

