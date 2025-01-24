package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Cluster.Companion.profiler
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan

class App

@Value("\${azure.app.client.jwk}")
lateinit var azureAppClientJwk: String
fun main(args: Array<String>) {
    runApplication<App>(*args) {
        getLogger(App::class.java).info("Azure app client jwk: $azureAppClientJwk")
        setAdditionalProfiles(*profiler)
    }
}