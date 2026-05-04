package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.TimingAspect
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature

class TimingAspectTest : BehaviorSpec({

    lateinit var registry: SimpleMeterRegistry
    lateinit var aspect: TimingAspect

    beforeEach {
        registry = SimpleMeterRegistry()
        aspect = TimingAspect(registry)
    }

    fun joinPoint(methodName: String = "intercept") = mockk<ProceedingJoinPoint> {
        every { signature } returns mockk<Signature> { every { name } returns methodName }
        every { proceed() } returns mockk<Any>()
    }

    Given("TimingAspect") {
        When("timeMethod kalles én gang") {
            Then("registreres mslogin-timer med riktig metodenavn-tagg") {
                aspect.timeMethod(joinPoint())
                registry.get("mslogin").tag("method", "intercept").timer().count() shouldBe 1
            }
        }
        When("timeMethod kalles tre ganger") {
            Then("akkumuleres tidsregistreringer") {
                val jp = joinPoint()
                repeat(3) { aspect.timeMethod(jp) }
                registry.get("mslogin").tag("method", "intercept").timer().count() shouldBe 3
            }
        }
        When("timeMethod kalles") {
            Then("videresendes kallet til joinPoint.proceed()") {
                val jp = joinPoint()
                aspect.timeMethod(jp)
                verify { jp.proceed() }
            }
        }
    }
})

