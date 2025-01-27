package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

abstract class AbstractPingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {

    override fun health() =
        runCatching {
            up(pingable.ping())
        }.getOrElse(::down)

    private fun up(status: Map<String, String>) = with(pingable) {
        if (isEnabled()) {
            Health.up()
                .withDetail("endpoint", pingEndpoint()).withDetails(status)
                .build()
        } else {
            Health.up()
                .withDetail("endpoint", pingEndpoint())
                .withDetail("status", "disabled")
                .build()
        }
    }

    private fun down(e: Throwable) = with(pingable) {
        Health.down()
            .withDetail("endpoint", pingEndpoint())
            .withException(e)
            .build()
    }


    override fun toString() = "${javaClass.simpleName} [pingable=$pingable]"
}