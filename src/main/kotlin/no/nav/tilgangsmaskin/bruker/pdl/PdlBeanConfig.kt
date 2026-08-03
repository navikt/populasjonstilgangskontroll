package no.nav.tilgangsmaskin.bruker.pdl

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.USER_INFO_CONFIG
import io.confluent.kafka.schemaregistry.client.security.basicauth.UserInfoCredentialProvider
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG
import io.micrometer.core.instrument.MeterRegistry
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlAvroEnvExtensions.schemaRegistryUrl
import no.nav.tilgangsmaskin.bruker.pdl.PdlAvroEnvExtensions.userInfo
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDLPIP
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.kafka.KafkaTypedDroppedMessageMeter
import no.nav.tilgangsmaskin.felles.security.OAuth2DownstreamUriCapturingInterceptor
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.kafka.autoconfigure.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.graphql.client.HttpSyncGraphQlClient.builder
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer

@Configuration
@NoCoverageAnalysis
class PdlBeanConfig {

    @Bean
    @Qualifier(PDLGRAPH)
    fun pdlGraphRestClient(builder: Builder,
                           mgr: OAuth2AuthorizedClientManager,
                           failureHandler: OAuth2AuthorizationFailureHandler) =
        builder
            .requestInterceptors {
                it.add(OAuth2DownstreamUriCapturingInterceptor())
                it.add(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
                it.add(OAuth2ClientHttpRequestInterceptor(mgr).apply {
                    setClientRegistrationIdResolver { PDLGRAPH }
                    setAuthorizationFailureHandler(failureHandler)
                })
            }
            .build()

    @Bean
    fun pdlPipGroupConfigurer() =
        RestClientHttpServiceGroupConfigurer {
            it.filterByName(PDLPIP).forEachClient { _, builder ->
                builder.requestInterceptor(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
            }
        }

    @Bean
    fun syncPdlGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig) =
        builder(client)
            .url(cfg.baseUri)
            .interceptors {
                it.addFirst(PdlGraphQLLoggingInterceptor())
            }.build()


    @Bean
    fun pdlGraphHealthIndicator(cfg: PdlGraphQLConfig, @Qualifier(PDLGRAPH) client: RestClient) =
        PingableHealthIndicator(cfg) {
            client.options()
                .uri(cfg.baseUri)
                .retrieve()
                .toBodilessEntity()
        }

    @Bean
    fun pdlPipHealthIndicator(cfg: PdlConfig, client: PdlPipClient) =
        PingableHealthIndicator(cfg, client::ping)

    @Bean
    fun pdlHendelseKafkaListenerConsumerFactory(props: KafkaProperties,
                                                env: Environment): ConsumerFactory<String, Personhendelse> =
        DefaultKafkaConsumerFactory(
            props.buildConsumerProperties().apply {
                put(GROUP_ID_CONFIG, PDL)
                put(VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer::class.java)
                put(SCHEMA_REGISTRY_URL_CONFIG, env.schemaRegistryUrl())
                put(SPECIFIC_AVRO_READER_CONFIG, true)
                put(BASIC_AUTH_CREDENTIALS_SOURCE, CREDENTIALS_SOURCE)
                put(USER_INFO_CONFIG, env.userInfo())
            }
        )

    @Bean(PDL_CONTAINER_FACTORY)
    fun pdlAvroListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Personhendelse>,
        commonErrorHandler: CommonErrorHandler,
    ) = ConcurrentKafkaListenerContainerFactory<String, Personhendelse>().apply {
        setConsumerFactory(consumerFactory)
        setCommonErrorHandler(commonErrorHandler)
    }

    @Bean
    fun pdlDroppedMessageMeter(registry: MeterRegistry) =
        object : KafkaTypedDroppedMessageMeter<Personhendelse>(registry, Personhendelse::class) {
            override fun formatEvent(event: Personhendelse) =
                "gradering=${event.adressebeskyttelse?.gradering ?: "UGRADERT"}, " +
                        "endringstype=${event.endringstype}, " +
                        "identer=${event.personidenter.map { it.maskFnr() }}"
        }

    companion object {
        const val PDL_GRADERING_FILTER = "pdlGraderingFilter"
        const val PDL_CONTAINER_FACTORY = "pdlContainerFactory"
        private val CREDENTIALS_SOURCE = UserInfoCredentialProvider().alias()
    }
}