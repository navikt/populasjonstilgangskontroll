package no.nav.tilgangsmaskin

import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.AUDITING_TIME_PROVIDER
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.profiler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.resilience.annotation.EnableResilientMethods
import java.time.Clock
import java.time.Clock.systemDefaultZone
import java.time.Instant
import java.util.Optional

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableResilientMethods
@ConditionalOnLocalOrTest
@Import(TestClockConfig::class)
class TestApp

/**
 * Minimal Clock-config for slice-tester (@DataJpaTest osv) som ikke laster FellesBeanConfig.
 * Duplikat av Clock-bønnene der, men nødvendig for at slice-testene skal få @CreatedDate / @LastModifiedDate til å fungere.
 */
@Configuration(proxyBeanMethods = false)
class TestClockConfig {
    @Bean
    fun clock() =
        systemDefaultZone()

    @Bean(AUDITING_TIME_PROVIDER)
    fun auditingDateTimeProvider(clock: Clock) =
        DateTimeProvider { Optional.of(Instant.now(clock)) }
}

fun main(args: Array<String>) {
    runApplication<App>(*args) {
        setAdditionalProfiles(*profiler)
    }
}