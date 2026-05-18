package no.nav.tilgangsmaskin.felles.cache

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

class CachePingableTest {

    private val connection = mockk<RedisConnection>(relaxed = true)
    private val connectionFactory = mockk<RedisConnectionFactory> {
        every { connection } returns this@CachePingableTest.connection
    }
    private val cfg = CacheConfig(
        username = "user",
        password = "pass",
        host = "localhost",
        port = 6379,
        timeout = Duration.ofSeconds(1)
    )
    private val pingable = CachePingable(connectionFactory, cfg)

    @Test
    fun `should return empty map when ping returns PONG`() {
        every { connection.ping() } returns "PONG"

        val result = pingable.ping()

        result shouldBe emptyMap<String, String>()
        verify { connection.close() }
    }

    @Test
    fun `should return empty map when ping returns pong lowercase`() {
        every { connection.ping() } returns "pong"

        val result = pingable.ping()

        result shouldBe emptyMap<String, String>()
    }

    @Test
    fun `should throw when ping does not return pong`() {
        every { connection.ping() } returns "ERROR"

        val exception = assertThrows<IllegalStateException> {
            pingable.ping()
        }

        exception.message shouldBe "Cache ping failed"
    }

    @Test
    fun `should throw when ping returns null`() {
        every { connection.ping() } returns null

        assertThrows<IllegalStateException> {
            pingable.ping()
        }
    }

    @Test
    fun `should have correct name and endpoint`() {
        pingable.name shouldBe "Cache"
        pingable.pingEndpoint.toString() shouldBe "localhost:6379"
    }
}

