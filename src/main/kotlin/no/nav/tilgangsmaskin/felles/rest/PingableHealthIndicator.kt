package no.nav.tilgangsmaskin.felles.rest

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class PingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {

    override fun health() =
        runCatching {
            pingable.ping()
            up()
        }.getOrElse(::down)

    private fun up() =
        with(pingable) {
            Health.up()
                .withDetail(ENDPOINT, pingEndpoint)
                .apply { if (!isEnabled) withDetail("enabled", "false") }
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