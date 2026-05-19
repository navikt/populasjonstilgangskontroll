package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import java.net.URI

class RestLoggingRequestInterceptorTest : BehaviorSpec({

    val interceptor = RestLoggingRequestInterceptor()
    val execution = mockk<ClientHttpRequestExecution>()

    Given("en vanlig request med body") {
        val request = MockClientHttpRequest(HttpMethod.POST, URI.create("https://api.nav.no/api/vedtak/123"))
        val body = """{"felt": "verdi"}""".toByteArray()
        val response = MockClientHttpResponse(ByteArray(0), HttpStatusCode.valueOf(200))

        every { execution.execute(request, body) } returns response

        When("intercept kalles") {
            val result = interceptor.intercept(request, body, execution)

            Then("returnerer responsen fra execution") {
                result shouldBe response
            }

            Then("kaller execution med request og body") {
                verify { execution.execute(request, body) }
            }
        }
    }

    Given("en request uten body") {
        val request = MockClientHttpRequest(HttpMethod.GET, URI.create("https://api.nav.no/api/ressurs"))
        val body = ByteArray(0)
        val response = MockClientHttpResponse(ByteArray(0), HttpStatusCode.valueOf(200))

        every { execution.execute(request, body) } returns response

        When("intercept kalles") {
            val result = interceptor.intercept(request, body, execution)

            Then("returnerer responsen fra execution") {
                result shouldBe response
            }
        }
    }

    Given("en monitoring-request") {
        val request = MockClientHttpRequest(HttpMethod.GET, URI.create("https://api.nav.no/monitoring/health"))
        val body = ByteArray(0)
        val response = MockClientHttpResponse(ByteArray(0), HttpStatusCode.valueOf(200))

        every { execution.execute(request, body) } returns response

        When("intercept kalles") {
            val result = interceptor.intercept(request, body, execution)

            Then("returnerer responsen uten å logge respons-status") {
                result shouldBe response
                verify { execution.execute(request, body) }
            }
        }
    }

    Given("execution kaster exception") {
        val request = MockClientHttpRequest(HttpMethod.GET, URI.create("https://api.nav.no/api/feil"))
        val body = ByteArray(0)

        every { execution.execute(request, body) } throws RuntimeException("Connection refused")

        When("intercept kalles") {
            Then("exception propageres") {
                val exception = runCatching {
                    interceptor.intercept(request, body, execution)
                }.exceptionOrNull()

                exception shouldBe RuntimeException("Connection refused")
            }
        }
    }
})
