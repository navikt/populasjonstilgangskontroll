package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class PingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {

    private val log = getLogger(javaClass)

    override fun health() =
        runCatching {
            if (!pingable.isEnabled) {
                return disabled()
            }
            pingable.ping()
            up()
        }.getOrElse {
            log.warn("Kunne ikke pinge ${pingable.isEnabled}", it)
            down(it)
        }

    private fun disabled() =
        Health.outOfService()
            .withDetail(ENDPOINT, pingable.pingEndpoint)
            .build()

    private fun up() =
        with(pingable) {
            Health.up()
                .withDetail(ENDPOINT, pingEndpoint)
                .build()
        }

    private fun down(e: Throwable) =
        with(pingable) {
            Health.down()
                .withDetail(ENDPOINT, pingEndpoint)
                .withException(e)
                .build()
        }

    override fun toString() = "${javaClass.simpleName} [pingable=$pingable]"

    companion object {
        const val ENDPOINT = "endpoint"
    }
}

