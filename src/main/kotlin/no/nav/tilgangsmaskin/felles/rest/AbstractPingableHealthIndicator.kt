package no.nav.tilgangsmaskin.felles.rest

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

abstract class AbstractPingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {

    override fun health() = runCatching {
        val respons = pingable.ping()
        if (respons is Map<*, *>) up(respons) else up()
    }.getOrElse(::down)

    private fun up(details: Map<Any,Any> = emptyMap()) = with(pingable) {
        if (isEnabled) {
            Health.up()
                .withDetail("endpoint", pingEndpoint)
                .withDetails(details)
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