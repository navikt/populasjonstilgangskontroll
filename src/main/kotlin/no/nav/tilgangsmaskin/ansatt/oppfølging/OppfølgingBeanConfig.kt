package no.nav.tilgangsmaskin.ansatt.oppfølging

import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.KafkaListenerErrorHandler

@Configuration
class OppfølgingBeanConfig {
    private val log = getLogger(javaClass)
    @Bean(OPPFØLGING_ERROR_HANDLER)
    fun oppfølgingErrorHandler() = KafkaListenerErrorHandler { msg, e ->
        val h = msg.payload as OppfølgingHendelse
        log.error("Feil ved behandling av hendelse: ${h.oppfolgingsperiodeUuid} (${h.sisteEndringsType})", e)
    }
    companion object {
        const val OPPFØLGING_ERROR_HANDLER = "oppfolgingErrorHandler"
    }
}