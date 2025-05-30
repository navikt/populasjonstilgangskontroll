package no.nav.tilgangsmaskin.felles.rest

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

abstract class AbstractPingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {

    override fun health() = runCatching {
        up((pingable.ping() as? Map<*, *>)?.map { (k, v) -> k.toString() to v.toString() }?.toMap() ?: emptyMap())
    }.getOrElse(::down)

    private fun up(details: Map<String,String> = emptyMap()) = with(pingable) {
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