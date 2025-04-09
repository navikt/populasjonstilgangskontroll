package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.http.HttpServletRequest
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.Include.defaultIncludes
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisConnectionUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class FellesBeanConfig(private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor) : CachingConfigurer, WebMvcConfigurer {

    private val log = getLogger(javaClass)

    @Bean
    fun jacksonCustomizer() = Jackson2ObjectMapperBuilderCustomizer {it.mixIn(OAuth2AccessTokenResponse::class.java, IgnoreUnknownMixin::class.java) }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin

    @Bean
    fun errorMessageSource() = ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
        }

    @Bean
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor) = RestClientCustomizer {
        it.requestFactory(HttpComponentsClientHttpRequestFactory())
        it.requestInterceptors {
            it.addFirst(interceptor)
        }
    }

    @Bean
    fun fellesRetryListener() = FellesRetryListener()

    @Bean
    @ConditionalOnNotProd
    fun traceRepository() = InMemoryHttpExchangeRepository()

    @Bean
    @ConditionalOnNotProd
    fun httpExchangesFilter(repository: HttpExchangeRepository) = object : HttpExchangesFilter(repository, defaultIncludes()) {
        override fun shouldNotFilter(request: HttpServletRequest) = request.servletPath.contains("monitoring")
    }

    @Bean
    fun redisHealthIndicator(cf: RedisConnectionFactory) = object : HealthIndicator  {
        override fun health() =
            RedisConnectionUtils.getConnection(cf).use { connection ->
                runCatching {
                    if (connection.ping().equals("PONG", ignoreCase = true)) {
                        Health.up().withDetail("Redis", "Connection is healthy").build()
                    } else {
                        Health.down().withDetail("Redis", "Ping failed").build()
                    }
                }.fold(
                    onSuccess = { it },
                    onFailure = { Health.down(it).withDetail("Redis", "Connection failed").build() }
                )
            }
    }

    @Bean
    fun redisTemplate(cf: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            connectionFactory = cf
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()
        }
    }


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}