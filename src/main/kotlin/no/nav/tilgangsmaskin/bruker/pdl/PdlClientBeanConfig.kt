package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.graphql.GraphQLErrorHandler
import no.nav.tilgangsmaskin.felles.rest.AbstractPingableHealthIndicator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@Configuration
class PdlClientBeanConfig {


    @Component
    @Primary
    class DefaultGraphQlErrorHandler : GraphQLErrorHandler

    @Bean
    @Qualifier(PDLGRAPH)
    fun pdlGraphRestClient(b: Builder) =
        b.requestInterceptors {
            it.add(headerAddingRequestInterceptor(BEHANDLINGSNUMMER))
        }.build()

    @Bean
    @Qualifier(PDLGRAPH)
    fun syncPdlGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig) =
        HttpSyncGraphQlClient.builder(client)
            .url(cfg.baseUri)
            .interceptor(LoggingGraphQLInterceptor())
            .build()

    @Bean
    @Qualifier(PDL)
    fun pdlRestClient(b: Builder) = b.build()

    @Bean
    fun pdlGraphHealthIndicator(a: PdlSyncGraphQLClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

    @Bean
    fun pdlHealthIndicator(a: PdlRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

    private class LoggingGraphQLInterceptor : SyncGraphQlClientInterceptor {

        private val log = LoggerFactory.getLogger(javaClass)

        override fun intercept(req: ClientGraphQlRequest, chain: SyncGraphQlClientInterceptor.Chain) =
            chain.next(req).also {
                log.trace(CONFIDENTIAL, "Eksekverer {} med variabler {}", req.document, req.variables)
            }
    }
}