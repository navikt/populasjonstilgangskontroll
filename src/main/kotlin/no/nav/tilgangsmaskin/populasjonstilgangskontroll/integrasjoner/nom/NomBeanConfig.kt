package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
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
            with(runCatching { BrukerId(record.value().personident) }) {
                if (isFailure) {
                    true.also {
                        log.warn("Ugyldig personident: ${record.value().personident.mask()} ble filtrert bort (${exceptionOrNull()?.message})",exceptionOrNull())
                    }
                } else false
            }
        }
}