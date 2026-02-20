package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.KafkaListenerErrorHandler

@Configuration
@ConfigurationProperties(OPPFØLGING)
class OppfølgingConfig: CachableRestConfig {
    override val navn = OPPFØLGING
    override val caches = setOf(OPPFØLGING_CACHE)

    private val log = getLogger(javaClass)

    @Bean(OPPFØLGING_ERROR_HANDLER)
    fun oppfølgingErrorHandler() = KafkaListenerErrorHandler { msg, e ->
        log.error("Feil ved prosessering av hendelse: ${(msg.payload as OppfølgingHendelse).oppfolgingsperiodeUuid}", e)
    }

    companion object {
        const val OPPFØLGING = "oppfolging"
        const val OPPFØLGING_ERROR_HANDLER = "oppfolgingErrorHandler"
        val OPPFØLGING_CACHE = CachableConfig(OPPFØLGING)
    }
}