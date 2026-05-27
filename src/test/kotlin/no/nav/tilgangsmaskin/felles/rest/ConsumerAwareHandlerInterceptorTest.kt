package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class ConsumerAwareHandlerInterceptorTest : BehaviorSpec({

    val token = mockk<Token>()
    lateinit var registry: SimpleMeterRegistry
    lateinit var interceptor: ConsumerAwareHandlerInterceptor

    beforeEach {
        registry = SimpleMeterRegistry()
        interceptor = ConsumerAwareHandlerInterceptor(token, registry)
        every { token.systemAndNs } returns "tilgangsmaskin:my-app"
        every { token.systemNavn } returns "my-app"
        every { token.ansattId } returns null
        MDC.clear()
    }

    Given("preHandle") {
        When("request mottas") {
            Then("legges consumerId i MDC") {
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                MDC.get(CONSUMER_ID) shouldBe "tilgangsmaskin:my-app"
            }
            Then("inkrementeres http_requests_by_remote_system med riktig remote_system-tagg") {
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                registry.get("http_requests_by_remote_system").tag("remote_system", "my-app").counter().count() shouldBe 1.0
            }
            Then("returneres true") {
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any()).shouldBeTrue()
            }
        }

        When("token har ansattId (OBO)") {
            Then("settes userId fra tokenet") {
                every { token.ansattId } returns AnsattId("Z999999")
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                MDC.get(USER_ID) shouldBe "Z999999"
            }
        }

        When("token mangler ansattId (CCF)") {
            Then("settes ikke userId i MDC av interceptoren — controlleren gjør det nedstrøms") {
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                MDC.get(USER_ID).shouldBeNull()
            }
        }
    }

    Given("afterCompletion") {
        When("request er ferdig") {
            Then("ryddes både consumerId og userId fra MDC") {
                every { token.ansattId } returns AnsattId("Z999999")
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()
                interceptor.preHandle(request, response, Any())
                MDC.get(CONSUMER_ID) shouldBe "tilgangsmaskin:my-app"
                MDC.get(USER_ID) shouldBe "Z999999"

                interceptor.afterCompletion(request, response, Any(), null)

                MDC.get(CONSUMER_ID).shouldBeNull()
                MDC.get(USER_ID).shouldBeNull()
            }
        }
    }
})
