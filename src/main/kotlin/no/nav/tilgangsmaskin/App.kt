package no.nav.tilgangsmaskin

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.profiler
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.local
import no.nav.tilgangsmaskin.regler.motor.RegelSett
import org.springframework.boot.SpringBootVersion
import org.springframework.boot.actuate.info.Info.Builder
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.SpringVersion
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableOAuth2Client(cacheEnabled = true)
@EnableCaching
@EnableRetry
@EnableJpaAuditing
@EnableScheduling
@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
class App

fun main(args: Array<String>) {
    runApplication<App>(*args) {
        setAdditionalProfiles(*profiler)
    }
}

@Component
class StartupInfoContributor(private val ctx: ConfigurableApplicationContext, vararg val regelsett: RegelSett) :
    InfoContributor {

    override fun contribute(builder: Builder) {
        with(ctx) {
            builder.withDetail(
                "extra-info", mapOf(
                    "Startup time" to startupDate.local(),
                    "Client ID" to environment.getProperty("azure.app.client.id"),
                    "Name" to environment.getProperty("spring.application.name"),
                    "Spring Boot version" to SpringBootVersion.getVersion(),
                    "Spring Framework version" to SpringVersion.getVersion()
                )
            )
            regelsett.forEach {
                builder.withDetail(it.beskrivelse, it.regler.map { r -> r.kortNavn })
            }
        }
    }
}