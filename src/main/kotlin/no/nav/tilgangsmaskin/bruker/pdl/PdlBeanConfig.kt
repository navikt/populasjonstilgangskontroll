package no.nav.tilgangsmaskin.bruker.pdl

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.USER_INFO_CONFIG
import io.confluent.kafka.schemaregistry.client.security.basicauth.UserInfoCredentialProvider
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG
import io.micrometer.core.instrument.MeterRegistry
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.utils.extensions.EnvExtensions.schemaRegistryUrl
import no.nav.tilgangsmaskin.felles.utils.extensions.EnvExtensions.userInfo
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.kafka.autoconfigure.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.graphql.client.HttpSyncGraphQlClient.builder
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS
import org.springframework.util.backoff.ExponentialBackOff
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@Configuration
@NoCoverageAnalysis
class PdlBeanConfig {

    @Bean
    @Qualifier(PDLGRAPH)
    fun pdlGraphRestClient(builder: Builder) =
        builder.requestInterceptors {
            it.add(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
        }.build()

    @Bean
    fun syncPdlGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig) =
        builder(client)
            .url(cfg.baseUri)
            .interceptors {
                it.addFirst(PdlGraphQLLoggingInterceptor())
            }.build()

    @Bean
    fun pdlPipClient(builder: Builder, cfg: PdlConfig) =
        createClient<PdlPipClient>(cfg, builder)

    @Bean
    fun pdlGraphQLPingClient(builder: Builder, cfg: PdlGraphQLConfig) =
        createClient<PdlGraphQLPingClient>(cfg, builder)

    @Bean
    fun pdlGraphHealthIndicator(cfg: PdlGraphQLConfig, client: PdlGraphQLPingClient) =
        PingableHealthIndicator(cfg, client::ping)

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
        meterRegistry: MeterRegistry,
    ) = ConcurrentKafkaListenerContainerFactory<String, Personhendelse>().apply {
        setConsumerFactory(consumerFactory)
        setCommonErrorHandler(
            DefaultErrorHandler(
                ExponentialBackOff(1_000L, 2.0).apply {
                    maxInterval = 60_000L          // taket per retry: 60 sek
                    maxElapsedTime = 600_000L      // gi opp etter 10 min totalt
                }
            ).apply {
                setRetryListeners(DroppedMessageMeter(meterRegistry))
            }
        )
    }

    companion object {
        const val PDL_GRADERING_FILTER = "pdlGraderingFilter"
        const val PDL_CONTAINER_FACTORY = "pdlContainerFactory"
        private val CREDENTIALS_SOURCE = UserInfoCredentialProvider().alias()
    }
}

private class DroppedMessageMeter(private val registry: MeterRegistry) : RetryListener {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun recovered(record: ConsumerRecord<*, *>, ex: Exception?) {
        registry.counter("kafka.message.dropped",
            "topic", record.topic(),
            "exception", ex?.javaClass?.simpleName ?: "unknown").increment()
        log.error("Ga opp Kafka-melding på topic=${record.topic()} partition=${record.partition()} offset=${record.offset()}: ${ex?.message}", ex)
    }

    override fun failedDelivery(record: ConsumerRecord<*, *>, ex: Exception?, deliveryAttempt: Int) {
        log.warn("Forsøk $deliveryAttempt feilet for melding på topic=${record.topic()} offset=${record.offset()}: ${ex?.message}")
    }
}
