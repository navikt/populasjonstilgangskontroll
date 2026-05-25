package no.nav.tilgangsmaskin

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheSizeAwareBean
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
@ConditionalOnGCP
@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
class App

fun main(args: Array<String>) {
    runApplication<App>(*args) {
        setAdditionalProfiles(*profiler)
    }
}

@Component
class StartupInfoContributor(private val sizes : CacheSizeAwareBean, private val ctx: ConfigurableApplicationContext, vararg val regelsett: RegelSett) :
    InfoContributor {

    override fun contribute(builder: Builder) {
        builder.withDetail("startup", ctx.startupDate.local())
        builder.withDetail("cacheSize med familie", sizes.size(PDL_MED_FAMILIE_CACHE))
        regelsett.filter { it.regler.isNotEmpty() }.forEach {
            builder.withDetail(it.beskrivelse, it.regler.map { regel -> "(${regel.javaClass.simpleName}) ${regel.kortNavn}" })
        }
    }
}