package no.nav.tilgangsmaskin.bruker.pdl

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.graphql.GraphQLErrorHandler
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.kafka.autoconfigure.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.graphql.client.SyncGraphQlClientInterceptor.Chain
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@Configuration
class PdlClientBeanConfig {
    private val log = getLogger(javaClass)

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
    fun syncPdlGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig,  interceptors: List<SyncGraphQlClientInterceptor>) =
        HttpSyncGraphQlClient.builder(client)
            .url(cfg.baseUri)
            .interceptors {
                it.addAll(interceptors)
            }.build()

    @Bean
    @Qualifier(PDL)
    fun pdlRestClient(b: Builder) = b.build()

    @Bean
    @ConditionalOnNotProd
    fun loggingGraphQLInterceptor() = object:  SyncGraphQlClientInterceptor {

        private val log = getLogger(javaClass)

        override fun intercept(req: ClientGraphQlRequest, chain:    Chain) =
            chain.next(req).also {
                log.trace("Eksekverer {} med variabler {}", req.document, req.variables)
            }
    }

    @Bean
    fun pdlGraphHealthIndicator(a: PdlSyncGraphQLClientAdapter) = PingableHealthIndicator(a)

    @Bean
    fun pdlHealthIndicator(a: PdlRestClientAdapter) = PingableHealthIndicator(a)

    @Bean
    @Qualifier(PDL)
    fun pslListenerContainerFactory(p : KafkaProperties, env: Environment) : ConcurrentKafkaListenerContainerFactory<String, Personhendelse> {
        val cf = ConcurrentKafkaListenerContainerFactory<String, Personhendelse>().apply {
            containerProperties.isObservationEnabled = true
            setConsumerFactory(DefaultKafkaConsumerFactory(p.buildConsumerProperties().apply {
                this[GROUP_ID_CONFIG] = "test"
                this["properties.specific.avro.reader"] = "true"
                this["properties.schema.registry.url"] = env.getRequiredProperty("kafka.schema.registry")
                this["properties.basic.auth.credentials.source"] = "USER_INFO"
                this["properties.basic.auth.user.info"] =
                    "${env.getRequiredProperty("kafka.schema.registry.user")}:${env.getRequiredProperty("kafka.schema.registry.password")}"
            }, StringDeserializer(), KafkaAvroDeserializer()))
        }
        return cf.also {
            log.info("CF er ${it.containerProperties}")
        }
    }

}