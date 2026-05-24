package no.nav.tilgangsmaskin.felles.cache

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

class CachePingableTest : BehaviorSpec({

    val connection = mockk<RedisConnection>(relaxed = true)
    val connectionFactory = mockk<RedisConnectionFactory> {
        every { this@mockk.connection } returns connection
    }
    val cfg = CacheConfig(
        username = "user",
        password = "pass",
        host = "localhost",
        port = 6379,
        timeout = Duration.ofSeconds(1)
    )
    val pingable = CachePingable(connectionFactory, cfg)

    Given("ping") {
        When("Redis returnerer PONG") {
            Then("kaster ingen feil og lukker connection") {
                every { connection.ping() } returns "PONG"
                pingable.ping()
                verify { connection.close() }
            }
        }
        When("Redis returnerer pong lowercase") {
            Then("kaster ingen feil") {
                every { connection.ping() } returns "pong"
                pingable.ping()
            }
        }
        When("Redis returnerer noe annet enn pong") {
            Then("kaster IllegalStateException") {
                every { connection.ping() } returns "ERROR"
                val ex = shouldThrow<IllegalStateException> { pingable.ping() }
                ex.message shouldBe "Cache ping failed"
            }
        }
        When("Redis returnerer null") {
            Then("kaster IllegalStateException") {
                every { connection.ping() } returns null
                shouldThrow<IllegalStateException> { pingable.ping() }
            }
        }
    }

    Given("metadata") {
        When("name og pingEndpoint sjekkes") {
            Then("har korrekte verdier") {
                pingable.name shouldBe "Cache"
                pingable.pingEndpoint.toString() shouldBe "localhost:6379"
            }
        }
    }
})
