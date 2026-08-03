package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDLPIP
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.health.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer

@Configuration
@NoCoverageAnalysis
class PdlPipBeanConfig {

    @Bean
    fun pdlPipServiceGroupConfigurer() =
        RestClientHttpServiceGroupConfigurer {
            it.filterByName(PDLPIP).forEachClient { _, builder ->
                builder.requestInterceptor(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
            }
        }

    @Bean
    fun pdlPipHealthIndicator(cfg: PdlPipConfig, client: PdlPipClient) =
        PingableHealthIndicator(cfg, client::ping)
}
