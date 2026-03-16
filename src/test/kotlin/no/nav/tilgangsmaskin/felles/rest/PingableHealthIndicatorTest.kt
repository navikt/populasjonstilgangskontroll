package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator.Companion.ENDPOINT
import org.springframework.boot.health.contributor.Status.DOWN
import org.springframework.boot.health.contributor.Status.OUT_OF_SERVICE
import org.springframework.boot.health.contributor.Status.UP

class PingableHealthIndicatorTest : DescribeSpec({

    class StubPingable(
        override val pingEndpoint: String = "http://example.com/ping",
        override val name: String = "test",
        override val isEnabled: Boolean = true,
        val onPing: () -> Any? = { }
    ) : Pingable {
        var pinged = false
        override fun ping(): Any? {
            pinged = true
            return onPing()
        }
    }

    describe("health") {

        it("returnerer UP når ping lykkes") {
            val pingable = StubPingable()
            val health = PingableHealthIndicator(pingable).health()

            health.status shouldBe UP
            health.details[ENDPOINT] shouldBe "http://example.com/ping"
            pingable.pinged shouldBe true
        }

        it("returnerer OUT_OF_SERVICE når isEnabled er false") {
            val pingable = StubPingable(isEnabled = false)
            val health = PingableHealthIndicator(pingable).health()

            health.status shouldBe OUT_OF_SERVICE
            health.details[ENDPOINT] shouldBe "http://example.com/ping"
            pingable.pinged shouldBe false
        }

        it("returnerer DOWN når ping kaster exception") {
            val pingable = StubPingable(onPing = { throw RuntimeException("Connection refused") })
            val health = PingableHealthIndicator(pingable).health()

            health.status shouldBe DOWN
            health.details[ENDPOINT] shouldBe "http://example.com/ping"
            health.details.values.map { it.toString() }.any { it.contains("Connection refused") } shouldBe true
        }

        it("inkluderer riktig pingEndpoint i DOWN-detaljer") {
            val pingable = StubPingable(
                pingEndpoint = "http://other.com/health",
                onPing = { throw RuntimeException("timeout") }
            )
            val health = PingableHealthIndicator(pingable).health()

            health.details[ENDPOINT] shouldBe "http://other.com/health"
        }
    }
})



