package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.context.ApplicationContext
import org.springframework.boot.actuate.info.Info.Builder
import org.springframework.core.SpringVersion
import org.springframework.boot.SpringBootVersion
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class StartupInfoContributor(private val ctx : ConfigurableApplicationContext) : InfoContributor {

    override fun contribute(builder : Builder) {
        builder.withDetail("extra-info", mapOf("Startup time" to ctx.startupDate.local(),
            "Client ID" to ctx.environment.getProperty("azure.app.client.id"),
            "Name" to ctx.environment.getProperty("spring.application.name"),
            "Spring Boot version" to SpringBootVersion.getVersion(),
            "Spring Framework version" to SpringVersion.getVersion()))
    }

    private fun Long.local(fmt : String = "yyyy-MM-dd HH:mm:ss") = LocalDateTime.ofInstant(Instant.ofEpochMilli(this),
        ZoneId.of("Europe/Oslo")).format(DateTimeFormatter.ofPattern(fmt))
}