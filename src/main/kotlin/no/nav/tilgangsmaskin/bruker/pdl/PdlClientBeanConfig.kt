package no.nav.tilgangsmaskin.bruker.pdl

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.USER_INFO_CONFIG
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.graphql.GraphQLErrorHandler
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.kafka.autoconfigure.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.core.env.getRequiredProperty
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.graphql.client.SyncGraphQlClientInterceptor.Chain
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS
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
    fun pdlHendelseKafkaListenerContainerFactory(props: KafkaProperties, env: Environment): ConsumerFactory<String, Personhendelse> =
        DefaultKafkaConsumerFactory(
            props.buildConsumerProperties().toMutableMap().apply {
                this[GROUP_ID_CONFIG] = "pdl-avro12345"
                this[VALUE_DESERIALIZER_CLASS] = KafkaAvroDeserializer::class.java
                this[SCHEMA_REGISTRY_URL_CONFIG] = env.schemaRegistryUrl()
                this[SPECIFIC_AVRO_READER_CONFIG] = true
                this[BASIC_AUTH_CREDENTIALS_SOURCE] = "USER_INFO"
                this[USER_INFO_CONFIG] = env.schemaRegistryUserInfo()
            }
        )

    private fun Environment.schemaRegistryUrl() =
        getRequiredProperty<String>("kafka.schema.registry")

    private fun Environment.schemaRegistryUserInfo() =
        "${getRequiredProperty<String>("kafka.schema.registry.user")}:${getRequiredProperty<String>("kafka.schema.registry.password")}"

    @Bean
    fun pdlAvroListenerContainerFactory(consumerFactory: ConsumerFactory<String, Personhendelse>) =
         ConcurrentKafkaListenerContainerFactory<String, Personhendelse>().apply {
            setConsumerFactory(consumerFactory)
    }
}