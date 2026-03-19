package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class ConsumerAwareHandlerInterceptorTest : DescribeSpec({

    val token = mockk<Token>()
    lateinit var registry: SimpleMeterRegistry
    lateinit var interceptor: ConsumerAwareHandlerInterceptor

    beforeEach {
        registry = SimpleMeterRegistry()
        interceptor = ConsumerAwareHandlerInterceptor(token, registry)
        every { token.systemAndNs } returns "tilgangsmaskin:my-app"
        every { token.systemNavn } returns "my-app"
        MDC.clear()
    }

    describe("preHandle") {
        it("legger consumerId i MDC") {
            interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
            MDC.get(CONSUMER_ID) shouldBe "tilgangsmaskin:my-app"
        }

        it("inkrementerer http_requests_by_remote_system med riktig remote_system-tagg") {
            interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
            registry.get("http_requests_by_remote_system")
                .tag("remote_system", "my-app")
                .counter()
                .count() shouldBe 1.0
        }

        it("returnerer true") {
            interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any()) shouldBe true
        }
    }
})

