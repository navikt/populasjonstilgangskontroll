package no.nav.tilgangsmaskin.felles

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.tilgang.Token
import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.ClusterAddingTimedAspectTest.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestConfig::class])
@AutoConfigureMetrics
@ApplyExtension(SpringExtension::class)
class ClusterAddingTimedAspectTest : DescribeSpec() {

    @MockkBean
    lateinit var token: Token

    @Autowired
    lateinit var registry: MeterRegistry

    @Autowired
    lateinit var timedService: TimedService

    init {
        beforeEach {
            every { token.cluster } returns "dev-gcp"
            every { token.systemNavn } returns "my-app"
        }

        describe("clusterAddingTimedAspect") {
            it("registrerer timer med cluster-, method- og client-tagg fra token") {
                timedService.execute()

                registry.get("test.execute")
                    .tag("cluster", "dev-gcp")
                    .tag("method", "execute")
                    .tag("client", "my-app")
                    .timer()
                    .count() shouldBeGreaterThan 0L
            }

            it("bruker oppdaterte verdier fra token per kall") {
                every { token.cluster } returns "prod-gcp"
                every { token.systemNavn } returns "annen-app"

                timedService.execute()

                registry.get("test.execute")
                    .tag("cluster", "prod-gcp")
                    .tag("client", "annen-app")
                    .timer()
                    .count() shouldBeGreaterThan 0L
            }
        }
    }

    @Configuration
    @EnableAspectJAutoProxy
    class TestConfig {
        @Bean fun timedService() = TimedService()

        @Bean fun timedAspect(registry: MeterRegistry, token: Token) =
            FellesBeanConfig(mockk(relaxed = true)).clusterAddingTimedAspect(registry, token)
    }

    open class TimedService {
        @Timed("test.execute")
        open fun execute() {}
    }
}

