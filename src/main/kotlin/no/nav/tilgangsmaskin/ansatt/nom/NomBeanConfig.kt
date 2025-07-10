package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.adapter.RecordFilterStrategy

@Configuration
class NomBeanConfig {


    @Bean
    fun fnrFilterStrategy() = RecordFilterStrategy<String, NomHendelse> {
        runCatching { BrukerId(it.value().personident) }.isFailure
    }
}