package no.nav.tilgangsmaskin.felles

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

abstract class AbstractPingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {

    override fun health() =
        runCatching {
            pingable.ping()
            up()
        }.getOrElse(::down)

    private fun up() = with(pingable) {
        if (isEnabled) {
            Health.up()
                .withDetail("endpoint", pingEndpoint)
                .build()
        } else {
            Health.up()
                .withDetail("endpoint", pingEndpoint)
                .withDetail("status", "disabled")
                .build()
        }
    }

    private fun down(e: Throwable) = with(pingable) {
        Health.down()
            .withDetail("endpoint", pingEndpoint)
            .withException(e)
            .build()
    }

    override fun toString() = "${javaClass.simpleName} [pingable=$pingable]"
}