package no.nav.tilgangsmaskin.felles.rest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import no.nav.tilgangsmaskin.felles.rest.RestDefaultErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import java.net.URI

class DefaultRestErrorHandlerTest : BehaviorSpec({

    val handler = RestDefaultErrorHandler()
    val uri = URI.create("http://test-service/api/resource")

    fun req(ident: String? = null) = MockClientHttpRequest(GET, uri).apply {
        ident?.let { headers.set(IDENTIFIKATOR, it) }
    }

    fun res(status: HttpStatus) = withStatus(status).createResponse(null)

    Given("handle - 404 Not Found") {
        When("request uten identifikator") {
            Then("kastes NotFoundRestException med riktig URI og null identifikator") {
                val ex = shouldThrow<NotFoundRestException> { handler.handle(req(), res(NOT_FOUND)) }
                ex.uri shouldBe uri
                ex.identifikator shouldBe null
            }
        }
        When("request med identifikator-header") {
            Then("kastes NotFoundRestException med identifikator fra header") {
                val ex = shouldThrow<NotFoundRestException> { handler.handle(req("12345678901"), res(NOT_FOUND)) }
                ex.identifikator shouldBe "12345678901"
            }
        }
    }

    Given("handle - 4xx klientfeil (ikke 404)") {
        When("400 Bad Request") {
            Then("kastes IrrecoverableRestException, ikke NotFoundRestException") {
                val ex = shouldThrow<IrrecoverableRestException> { handler.handle(req(), res(BAD_REQUEST)) }
                ex.shouldBeInstanceOf<IrrecoverableRestException>()
                (ex is NotFoundRestException) shouldBe false
            }
        }
        When("403 Forbidden") {
            Then("kastes IrrecoverableRestException") {
                shouldThrow<IrrecoverableRestException> { handler.handle(req(), res(FORBIDDEN)) }
            }
        }
    }

    Given("handle - 5xx serverfeil") {
        When("500 Internal Server Error") {
            Then("kastes RecoverableRestException, ikke IrrecoverableRestException") {
                val ex = shouldThrow<RecoverableRestException> { handler.handle(req(), res(INTERNAL_SERVER_ERROR)) }
                ex.shouldNotBeInstanceOf<IrrecoverableRestException>()
            }
        }
        When("503 Service Unavailable") {
            Then("kastes RecoverableRestException") {
                shouldThrow<RecoverableRestException> { handler.handle(req(), res(SERVICE_UNAVAILABLE)) }
            }
        }
    }
})
