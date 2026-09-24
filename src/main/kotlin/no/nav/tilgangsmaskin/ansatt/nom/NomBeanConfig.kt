package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.ansatt.nom.NomGraphQLConfig.Companion.NOMGRAPH
import no.nav.tilgangsmaskin.ansatt.nom.NomHendelseKonsument.Companion.NOM_FNR_FILTER_STRATEGY
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLLoggingInterceptor
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.kafka.KafkaTypedDroppedMessageMeter
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.security.OAuth2DownstreamUriCapturingInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpSyncGraphQlClient.builder
import org.springframework.kafka.listener.adapter.RecordFilterStrategy
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

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

    @Bean
    @Qualifier(NOMGRAPH)
    fun nomGraphRestClient(builder: Builder, mgr: OAuth2AuthorizedClientManager, failureHandler: OAuth2AuthorizationFailureHandler) =
        builder
            .requestInterceptors {
                it.add(OAuth2DownstreamUriCapturingInterceptor())
                it.add(OAuth2ClientHttpRequestInterceptor(mgr).apply {
                    setClientRegistrationIdResolver { NOMGRAPH }
                    setAuthorizationFailureHandler(failureHandler)
                })
            }
            .build()

    @Bean
    @Qualifier(NOMGRAPH)
    fun nomGraphSyncGraphQLClient(@Qualifier(NOMGRAPH) client: RestClient, cfg: NomGraphQLConfig) =
        builder(client)
            .url(cfg.baseUri)
            .interceptors {
                it.addFirst(PdlGraphQLLoggingInterceptor())
            }.build()
}

