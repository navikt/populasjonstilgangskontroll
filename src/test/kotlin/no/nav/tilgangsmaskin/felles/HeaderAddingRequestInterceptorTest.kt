package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.OK
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import java.net.URI

class HeaderAddingRequestInterceptorTest : DescribeSpec({

    val execution = mockk<ClientHttpRequestExecution>()

    beforeEach {
        every { execution.execute(any(), any()) } returns MockClientHttpResponse(ByteArray(0), OK)
    }

    fun request() = MockClientHttpRequest(GET, URI.create("/test"))

    describe("headerAddingRequestInterceptor") {
        it("legger til angitte headere i requesten") {
            val request = request()
            val interceptor = headerAddingRequestInterceptor("X-Test" to "verdi1", "X-Annet" to "verdi2")
            interceptor.intercept(request, ByteArray(0), execution)
            request.headers["X-Test"] shouldBe listOf("verdi1")
            request.headers["X-Annet"] shouldBe listOf("verdi2")
        }

        it("kaller videre til neste i kjeden") {
            val request = request()
            val body = ByteArray(0)
            val interceptor = headerAddingRequestInterceptor("X-Test" to "verdi")
            interceptor.intercept(request, body, execution)
            verify { execution.execute(request, body) }
        }

        it("legger til ingen headere når ingen verdier er angitt") {
            val request = request()
            val interceptor = headerAddingRequestInterceptor()
            interceptor.intercept(request, ByteArray(0), execution)
            request.headers.isEmpty shouldBe true
        }
    }
})
