package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Cluster.Companion.profiler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableRetry
class TestApp

fun main(args: Array<String>) {
    runApplication<App>(*args) {
        setAdditionalProfiles(*profiler)
    }
}