package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.AbstractHealthIndicator

class PingableHealthIndicator(private val pingable: Pingable) : AbstractHealthIndicator() {

    private val log = getLogger(javaClass)

    override fun doHealthCheck(builder: Health.Builder) {
        if (!pingable.isEnabled) {
            builder.outOfService()
                .withDetail(ENDPOINT, pingable.pingEndpoint)
            return
        }
        runCatching {
            pingable.ping()
            builder.up()
                .withDetail(ENDPOINT, pingable.pingEndpoint)
        }.getOrElse {
            log.warn("Kunne ikke pinge ${pingable.pingEndpoint}", it)
            builder.down(it)
                .withDetail(ENDPOINT, pingable.pingEndpoint)
        }
    }

    override fun toString() = "${javaClass.simpleName} [pingable=$pingable]"

    companion object {
        const val ENDPOINT = "endpoint"
    }
}


