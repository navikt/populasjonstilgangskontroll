package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomHendelseKonsument.Companion.NOM_ERROR_HANDLER
import no.nav.tilgangsmaskin.ansatt.nom.NomHendelseKonsument.Companion.NOM_FNR_FILTER_STRATEGY
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.KafkaListenerErrorHandler
import org.springframework.kafka.listener.adapter.RecordFilterStrategy

@Configuration
class NomBeanConfig {

    private val log = getLogger(javaClass)


    @Bean(NOM_FNR_FILTER_STRATEGY)
    fun nomFnrFilterStrategy() =
        RecordFilterStrategy<String, NomHendelse> {
            runCatching {
                BrukerId(it.value().personident)
            }.isFailure
        }

    @Bean(NOM_ERROR_HANDLER)
    fun nomErrorHandler() = KafkaListenerErrorHandler { msg, e ->
        log.error("Feil ved behandling av hendelse: ${msg.payload as NomHendelse}", e)
    }
}