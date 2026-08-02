package no.nav.tilgangsmaskin.felles.rest

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.getBean
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import java.util.LinkedHashMap

@TestConfiguration
class OAuth2ClientTestConfig {

    @Bean
    fun restClientCustomizer() = RestClientCustomizer { c ->
        c.defaultHeader("Jalla", "42")
        c.defaultStatusHandler(HttpStatusCode::isError, RestDefaultErrorHandler()::handle)
    }

    @Bean
    fun restClientGroupCustomizer(
        customizers: ObjectProvider<RestClientCustomizer>,
        env: Environment,
        restConfigs: ObjectProvider<RestConfig>
    ) =
        RestClientHttpServiceGroupConfigurer { groups ->
            val baseUrisByName = mutableMapOf<String, String>()
            restConfigs.orderedStream().forEach { cfg ->
                baseUrisByName[cfg.name] = cfg.baseUri.toString()
            }
            groups.forEachClient { group, builder ->
                env.getProperty("spring.http.serviceclient.${group.name()}.base-url")
                    ?.takeIf { it.isNotBlank() }
                    ?.let(builder::baseUrl)
                    ?: baseUrisByName[group.name()]?.let(builder::baseUrl)
                customizers.orderedStream().forEach { it.customize(builder) }
            }
        }

    companion object {
        private const val PROPERTY_SOURCE_NAME = "oauth2ClientTestConfig"

        @Bean
        @JvmStatic
        fun registerServiceClientBaseUrlsEarly() = BeanFactoryPostProcessor { beanFactory ->
            val env = beanFactory.getBean<ConfigurableEnvironment>()
            if (!env.propertySources.contains(PROPERTY_SOURCE_NAME)) {
                val props = LinkedHashMap<String, Any>()
                props["spring.http.serviceclient.pdl.base-url"] = "http://pdl-api.pdl"
                props["spring.http.serviceclient.pdlpip.base-url"] = "http://pdl-api.pdl"
                props["spring.http.serviceclient.pdlgraph.base-url"] = "http://pdl-api.pdl"
                props["spring.http.serviceclient.skjerming.base-url"] = "http://skjermede-personer-pip.nom"
                props["spring.http.serviceclient.entraproxy.base-url"] = "http://entra-proxy.sikkerhetstjenesten"
                props["spring.http.serviceclient.graph.base-url"] = "https://graph.microsoft.com/v1.0"
                props["spring.http.serviceclient.verge.base-url"] = "http://repr-api.repr"
                env.propertySources.addFirst(MapPropertySource(PROPERTY_SOURCE_NAME, props))
            }
        }
    }
}
