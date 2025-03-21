package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.adapter.RecordFilterStrategy

@Configuration
class NomBeanConfig {

    private val log = LoggerFactory.getLogger(NomBeanConfig::class.java)

    @Bean
    fun fnrFilterStrategy(): RecordFilterStrategy<String, NomHendelse> =
        RecordFilterStrategy { record ->
            runCatching { BrukerId(record.value().personident) }.isFailure.also {
                if (it) {
                    log.warn("Ugyldig personident: ${record.value().personident} ble filtrert bort")
                }
            }
        }
}