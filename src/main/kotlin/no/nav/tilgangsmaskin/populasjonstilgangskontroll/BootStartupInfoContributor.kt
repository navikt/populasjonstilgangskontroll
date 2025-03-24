package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett
import org.springframework.boot.SpringBootVersion
import org.springframework.boot.actuate.info.Info.Builder
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.SpringVersion
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class BootStartupInfoContributor(private val ctx : ConfigurableApplicationContext, vararg val regelsett: RegelSett) : InfoContributor {

    override fun contribute(builder : Builder) {
        with(ctx)    {
            builder.withDetail("extra-info", mapOf("Startup time" to startupDate.local(),
                "Client ID" to environment.getProperty("azure.app.client.id"),
                "Name" to environment.getProperty("spring.application.name"),
                "Spring Boot version" to SpringBootVersion.getVersion(),
                "Spring Framework version" to SpringVersion.getVersion()))
            regelsett.forEach {
                builder.withDetail(it.beskrivelse, it.regler.map {r -> r.metadata.kortNavn })
            }
        }
    }

    private fun Long.local(fmt : String = "yyyy-MM-dd HH:mm:ss") = LocalDateTime.ofInstant(Instant.ofEpochMilli(this),
        ZoneId.of("Europe/Oslo")).format(DateTimeFormatter.ofPattern(fmt))
}