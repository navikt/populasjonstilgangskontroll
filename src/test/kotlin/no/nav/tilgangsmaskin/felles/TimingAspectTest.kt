package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.TimingAspect
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature

class TimingAspectTest : DescribeSpec({

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

    describe("TimingAspect") {
        it("registrerer mslogin-timer med riktig metodenavn-tagg") {
            aspect.timeMethod(joinPoint())
            registry.get("mslogin").tag("method", "intercept").timer().count() shouldBe 1
        }

        it("akkumulerer tidsregistreringer ved gjentatte kall") {
            val jp = joinPoint()
            repeat(3) { aspect.timeMethod(jp) }
            registry.get("mslogin").tag("method", "intercept").timer().count() shouldBe 3
        }

        it("videresender kallet til joinPoint.proceed()") {
            val jp = joinPoint()
            aspect.timeMethod(jp)
            verify { jp.proceed() }
        }
    }
})

