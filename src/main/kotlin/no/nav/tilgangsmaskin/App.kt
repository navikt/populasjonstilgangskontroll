package no.nav.tilgangsmaskin

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
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
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableOAuth2Client(cacheEnabled = true)
@EnableCaching
@EnableResilientMethods
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
                "info", mapOf(
                    "Cluster" to current.clusterName,
                    "Startup" to startupDate.local(),
                    "Name" to environment.getProperty("spring.application.name"),
                    ))
            if (!isProd) {
                builder.withDetail("dev-info", mapOf(
                    "Client ID" to environment.getProperty("azure.app.client.id"),
                    "Spring Boot version" to SpringBootVersion.getVersion(),
                    "Spring Framework version" to SpringVersion.getVersion(),
                    "Java version" to environment.getProperty("java.version"),
                    "Java runtime version" to environment.getProperty("java.runtime.version"),
                    "Java vendor" to environment.getProperty("java.vm.vendor")
                    ))
            }
            regelsett.forEach {
                builder.withDetail(it.beskrivelse, it.regler.map { "(${it.javaClass.simpleName}) ${it.kortNavn}" })
            }
        }
    }
}