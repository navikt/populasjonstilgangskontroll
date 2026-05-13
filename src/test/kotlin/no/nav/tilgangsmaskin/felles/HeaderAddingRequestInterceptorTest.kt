package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.OK
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import java.net.URI

class HeaderAddingRequestInterceptorTest : BehaviorSpec({

    val execution = mockk<ClientHttpRequestExecution>()

    beforeEach {
        every { execution.execute(any(), any()) } returns MockClientHttpResponse(ByteArray(0), OK)
    }

    fun request() = MockClientHttpRequest(GET, URI.create("/test"))

    Given("headerAddingRequestInterceptor") {
        When("headere er angitt") {
            Then("legges de til i requesten") {
                val request = request()
                RestHeaderAddingRequestInterceptor("X-Test" to "verdi1", "X-Annet" to "verdi2")
                    .intercept(request, ByteArray(0), execution)
                request.headers["X-Test"] shouldBe listOf("verdi1")
                request.headers["X-Annet"] shouldBe listOf("verdi2")
            }
        }
        When("interceptor kjøres") {
            Then("videresendes kallet til neste i kjeden") {
                val request = request()
                val body = ByteArray(0)
                RestHeaderAddingRequestInterceptor("X-Test" to "verdi").intercept(request, body, execution)
                verify { execution.execute(request, body) }
            }
        }
        When("ingen verdier er angitt") {
            Then("legges ingen headere til") {
                val request = request()
                RestHeaderAddingRequestInterceptor().intercept(request, ByteArray(0), execution)
                request.headers.isEmpty shouldBe true
            }
        }
    }
})
