package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.lang.reflect.Method
import kotlin.toString

@Configuration
class FellesBeanConfig : CachingConfigurer {

    private val log = LoggerFactory.getLogger(FellesBeanConfig::class.java)

    @Bean
    fun errorMessageSource() = ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
        }
    @Bean
    fun fellesRetryListener() = FellesRetryListener()

    @Bean
    fun caffeine() = Caffeine.newBuilder()
        .recordStats()
        .removalListener { key: Any?, value: Any?, cause -> log.trace(CONFIDENTIAL,"Cache removal key={}, value={}, cause={}", key, value, cause)
    }.build<Any, Any>()
    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}

