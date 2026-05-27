package no.nav.tilgangsmaskin

import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import no.nav.tilgangsmaskin.felles.ClockConfig
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.profiler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.resilience.annotation.EnableResilientMethods

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableResilientMethods
@ConditionalOnLocalOrTest
@Import(ClockConfig::class)
class TestApp

fun main(args: Array<String>) {
    runApplication<App>(*args) {
        setAdditionalProfiles(*profiler)
    }
}