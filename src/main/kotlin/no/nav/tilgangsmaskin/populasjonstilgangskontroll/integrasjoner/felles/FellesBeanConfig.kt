package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.Include
import org.springframework.boot.actuate.web.exchanges.Include.defaultIncludes
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.stereotype.Component


@Configuration
class FellesBeanConfig : CachingConfigurer {

    private val log = LoggerFactory.getLogger(FellesBeanConfig::class.java)

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
    fun fellesRetryListener() = FellesRetryListener()

    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)

    @Bean
    @ConditionalOnNotProd
    fun traceRepository() = InMemoryHttpExchangeRepository()

    @Bean
    @ConditionalOnNotProd
    fun httpExchangesFilter(repository: HttpExchangeRepository) = object : HttpExchangesFilter(repository, defaultIncludes()) {
        override fun shouldNotFilter(request: HttpServletRequest) = request.servletPath.contains("monitoring")
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
            this.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .removalListener {
                    key, value, cause -> log.trace(CONFIDENTIAL,"Cache removal key={}, value={}, cause={}", key, value, cause) })
        }
}


