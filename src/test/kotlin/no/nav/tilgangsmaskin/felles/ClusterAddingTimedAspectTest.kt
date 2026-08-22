package no.nav.tilgangsmaskin.felles

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.micrometer.core.annotation.Timed
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import no.nav.tilgangsmaskin.felles.ClusterAddingTimedAspectTest.TestConfig
import no.nav.tilgangsmaskin.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.rest.health.ObservabilityBeanConfig
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestConfig::class, ObservabilityBeanConfig::class])
@AutoConfigureMetrics
class ClusterAddingTimedAspectTest(
    private val registry: MeterRegistry,
    private val aspect: TimedAspect,
    private val timedService: TimedService,
) : BehaviorSpec() {

    @MockkBean
    lateinit var authContext: AuthContext

    init {
        beforeEach {
            every { authContext.cluster } returns "dev-gcp"
            every { authContext.systemNavn } returns "my-app"
        }

        Given("clusterAddingTimedAspect") {
            When("tjeneste kalles") {
                Then("registreres timer med cluster-, method- og client-tagg fra token") {
                    timedService.execute()
                    registry.get("test.execute")
                        .tag("cluster", "dev-gcp")
                        .tag("method", "execute")
                        .tag("client", "my-app")
                        .timer().count() shouldBeGreaterThan 0L
                }
            }
            When("token-verdier endres mellom kall") {
                Then("brukes oppdaterte verdier per kall") {
                    every { authContext.cluster } returns "prod-gcp"
                    every { authContext.systemNavn } returns "annen-app"
                    timedService.execute()
                    registry.get("test.execute")
                        .tag("cluster", "prod-gcp")
                        .tag("client", "annen-app")
                        .timer().count() shouldBeGreaterThan 0L
                }
            }
        }
    }

    @Configuration
    class TestConfig {
        @Bean
        fun timedService() = TimedService()
    }

    open class TimedService {
        @Timed("test.execute")
        open fun execute() {}
    }
}
