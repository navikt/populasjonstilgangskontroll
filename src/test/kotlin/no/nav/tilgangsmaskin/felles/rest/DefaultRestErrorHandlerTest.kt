package no.nav.tilgangsmaskin.felles.rest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
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

class DefaultRestErrorHandlerTest : DescribeSpec({

    val handler = DefaultRestErrorHandler()
    val uri = URI.create("http://test-service/api/resource")

    fun req(ident: String? = null) = MockClientHttpRequest(GET, uri).apply {
        ident?.let { headers.set(IDENTIFIKATOR, it) }
    }

    fun res(status: HttpStatus) = withStatus(status).createResponse(null)

    describe("handle") {

        describe("404 Not Found") {

            it("kaster NotFoundRestException") {
                shouldThrow<NotFoundRestException> {
                    handler.handle(req(), res(NOT_FOUND))
                }
            }

            it("NotFoundRestException inneholder riktig URI") {
                val ex = shouldThrow<NotFoundRestException> {
                    handler.handle(req(), res(NOT_FOUND))
                }
                ex.uri shouldBe uri
            }

            it("NotFoundRestException inneholder identifikator fra header") {
                val ex = shouldThrow<NotFoundRestException> {
                    handler.handle(req("12345678901"), res(NOT_FOUND))
                }
                ex.identifikator shouldBe "12345678901"
            }

            it("NotFoundRestException har null identifikator når header mangler") {
                val ex = shouldThrow<NotFoundRestException> {
                    handler.handle(req(), res(NOT_FOUND))
                }
                ex.identifikator shouldBe null
            }
        }

        describe("4xx klientfeil (ikke 404)") {

            it("400 Bad Request kaster IrrecoverableRestException") {
                shouldThrow<IrrecoverableRestException> {
                    handler.handle(req(), res(BAD_REQUEST))
                }
            }

            it("403 Forbidden kaster IrrecoverableRestException") {
                shouldThrow<IrrecoverableRestException> {
                    handler.handle(req(), res(FORBIDDEN))
                }
            }

            it("IrrecoverableRestException er ikke NotFoundRestException for 400") {
                val ex = shouldThrow<IrrecoverableRestException> {
                    handler.handle(req(), res(BAD_REQUEST))
                }
                ex.shouldBeInstanceOf<IrrecoverableRestException>()
                (ex is NotFoundRestException) shouldBe false
            }
        }

        describe("5xx serverfeil") {

            it("500 Internal Server Error kaster RecoverableRestException") {
                shouldThrow<RecoverableRestException> {
                    handler.handle(req(), res(INTERNAL_SERVER_ERROR))
                }
            }

            it("503 Service Unavailable kaster RecoverableRestException") {
                shouldThrow<RecoverableRestException> {
                    handler.handle(req(), res(SERVICE_UNAVAILABLE))
                }
            }

            it("RecoverableRestException er ikke IrrecoverableRestException") {
                val ex = shouldThrow<RecoverableRestException> {
                    handler.handle(req(), res(INTERNAL_SERVER_ERROR))
                }
                ex.shouldNotBeInstanceOf<IrrecoverableRestException>()
            }
        }
    }
})
