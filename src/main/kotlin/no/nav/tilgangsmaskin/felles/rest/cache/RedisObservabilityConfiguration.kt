package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.resource.ClientResources
import io.lettuce.core.tracing.MicrometerTracing
import io.micrometer.observation.ObservationRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

//@Configuration
class RedisObservabilityConfiguration  {
//@Bean
fun clientResources(observationRegistry: ObservationRegistry) =
    ClientResources.builder()
        .tracing(MicrometerTracing(observationRegistry, "cache.redis"))
        .build()

   // @Bean
    fun  lettuceConnectionFactory(@Value("\${valkey.host.cache}") host: String, @Value("\${valkey.port.cache}") port: String, clientResources: ClientResources) : LettuceConnectionFactory {
        val clientConfig = LettuceClientConfiguration.builder()
            .clientResources(clientResources).build();
        val redisConfiguration = RedisStandaloneConfiguration(host, port.toInt())
        return LettuceConnectionFactory(redisConfiguration, clientConfig);
    }
}