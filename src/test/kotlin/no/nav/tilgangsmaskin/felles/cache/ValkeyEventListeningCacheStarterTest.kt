package no.nav.tilgangsmaskin.felles.cache

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.utils.LederUtvelger.LeaderChangedEvent
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import java.net.InetAddress

class ValkeyEventListeningCacheStarterTest : BehaviorSpec({

    val container = mockk<RedisMessageListenerContainer>()
    val starter = ValkeyEventListeningCacheStarter(container)

    beforeEach {
        clearMocks(container)
        every { container.isRunning } returns false
    }

    Given("Valkey keyspace listener starter") {
        When("instansen blir leder") {
            Then("startes containeren") {
                justRun { container.start() }

                starter.onApplicationEvent(
                    LeaderChangedEvent(
                        Any(),
                        InetAddress.getLocalHost().hostName,
                    ),
                )

                verify { container.start() }
            }
        }

        When("start feiler midlertidig") {
            Then("kastes ikke exception") {
                every { container.start() } throws RuntimeException("boom")

                shouldNotThrowAny {
                    starter.retryStart()
                }

                verify { container.start() }
            }
        }
    }
})
