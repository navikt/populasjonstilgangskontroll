package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.http.HttpServletRequest
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.client.core.OAuth2ClientException
import no.nav.security.token.support.client.core.http.OAuth2HttpClient
import no.nav.security.token.support.client.core.http.OAuth2HttpRequest
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory
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
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class FellesBeanConfig(private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor) : CachingConfigurer, WebMvcConfigurer {

    private val log = LoggerFactory.getLogger(FellesBeanConfig::class.java)

    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)


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

    override fun cacheManager()   =
        CaffeineCacheManager().apply {
            setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .removalListener {
                    key, value, cause -> log.trace("Cache removal key={}, value={}, cause={}", key, value, cause) })
        }
}

@Component
 class DefaultOAuth2HttpClient : OAuth2HttpClient {

    val restClient = RestClient.builder()
        .requestFactory(HttpComponentsClientHttpRequestFactory())
        .build()
    override fun post(req: OAuth2HttpRequest) =
        restClient.post()
            .uri(req.tokenEndpointUrl)
            .headers { it.addAll(headers(req)) }
            .body(LinkedMultiValueMap<String, String>().apply {
                setAll(req.formParameters)
            }).retrieve()
            .onStatus({ it.isError }) { _, response ->
                throw OAuth2ClientException("Received ${response.statusCode} from ${req.tokenEndpointUrl}")
            }
            .body<OAuth2AccessTokenResponse>() ?: throw OAuth2ClientException("No body in response from ${req.tokenEndpointUrl}")

    private fun headers(req: OAuth2HttpRequest): HttpHeaders = HttpHeaders().apply { putAll(req.oAuth2HttpHeaders.headers) }

    override fun toString() = "${javaClass.simpleName}  [restClient=$restClient]"
}