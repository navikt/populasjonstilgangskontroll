package no.nav.tilgangsmaskin

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.AUDITING_TIME_PROVIDER
import no.nav.tilgangsmaskin.felles.cache.CacheSizeAware
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.profiler
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.local
import no.nav.tilgangsmaskin.regler.motor.RegelSett
import org.springframework.boot.actuate.info.Info.Builder
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableOAuth2Client(cacheEnabled = true)
@EnableCaching
@EnableResilientMethods
@EnableJpaAuditing(dateTimeProviderRef = AUDITING_TIME_PROVIDER)
@EnableScheduling
@ConditionalOnGCP
@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
class App

fun main(args: Array<String>) {
    runApplication<App>(*args) {
        setAdditionalProfiles(*profiler)
    }
}

@Component
class StartupInfoContributor(private val caches : CacheSizeAware, private val ctx: ConfigurableApplicationContext, vararg val regelsett: RegelSett) :
    InfoContributor {

    override fun contribute(builder: Builder) {
        builder.withDetail("startup", ctx.startupDate.local())
        builder.withDetail("cache størrelser", caches.sizes())
        regelsett.filter { it.regler.isNotEmpty() }.forEach {
            builder.withDetail(it.beskrivelse, it.regler.map { regel -> "(${regel.javaClass.simpleName}) ${regel.kortNavn}" })
        }
    }
}