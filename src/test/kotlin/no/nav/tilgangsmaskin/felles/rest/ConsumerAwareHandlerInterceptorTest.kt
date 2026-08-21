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
import no.nav.tilgangsmaskin.felles.rest.TokenType.CCF
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import no.nav.tilgangsmaskin.felles.rest.TokenType.UNAUTHENTICATED
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
        every { token.type } returns CCF
        MDC.clear()
    }

    Given("preHandle") {
        When("request mottas") {
            Then("legges consumerId i MDC") {
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                MDC.get(CONSUMER_ID) shouldBe "tilgangsmaskin:my-app"
            }
            Then("inkrementeres http_requests_by_remote_system med riktig remote_system- og type-tagg") {
                val request = MockHttpServletRequest("POST", "/api/v1/regler")
                interceptor.preHandle(request, MockHttpServletResponse(), Any())
                registry.get("http_requests_by_remote_system").tag("remote_system", "my-app").tag("type", "enkelt").counter().count() shouldBe 1.0
            }
            Then("brukes bulk-tagg når pathen inneholder bulk") {
                val request = MockHttpServletRequest("POST", "/api/v1/bulk/obo")
                interceptor.preHandle(request, MockHttpServletResponse(), Any())
                registry.get("http_requests_by_remote_system").tag("remote_system", "my-app").tag("type", "bulk").counter().count() shouldBe 1.0
            }
            Then("returneres true") {
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any()).shouldBeTrue()
            }
        }

        When("token har ansattId (OBO)") {
            Then("settes userId fra tokenet") {
                every { token.ansattId } returns AnsattId("Z999999")
                every { token.type } returns OBO
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                MDC.get(USER_ID) shouldBe "Z999999"
            }
        }

        When("token mangler ansattId (CCF)") {
            Then("settes ikke userId i MDC av interceptoren — controlleren gjør det nedstrøms") {
                every { token.type } returns CCF
                interceptor.preHandle(MockHttpServletRequest(), MockHttpServletResponse(), Any())
                MDC.get(USER_ID).shouldBeNull()
            }
        }

        When("token er uautentisert") {
            Then("skippes metrikken") {
                every { token.type } returns UNAUTHENTICATED
                interceptor.preHandle(MockHttpServletRequest("POST", "/api/v1/bulk/obo"), MockHttpServletResponse(), Any())
                registry.find("http_requests_by_remote_system").counter().shouldBeNull()
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
