package no.nav.tilgangsmaskin.felles.graphql

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.bruker.pdl.PdlClientBeanConfig.DefaultGraphQlErrorHandler
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.graphql.ResponseError
import org.springframework.graphql.client.ClientGraphQlResponse
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.client.GraphQlTransportException
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import java.net.URI

class GraphQLErrorHandlerTest : DescribeSpec({

    val handler = DefaultGraphQlErrorHandler()
    val uri = URI.create("http://test/graphql")

    fun fieldAccessException(vararg errors: ResponseError): FieldAccessException {
        val response = mockk<ClientGraphQlResponse> {
            every { this@mockk.errors } returns errors.toList()
        }
        return FieldAccessException(mockk(), response, mockk(relaxed = true))
    }

    fun responseError(code: String) = mockk<ResponseError> {
        every { extensions } returns mapOf("code" to code)
    }

    describe("handle") {

        describe("FieldAccessException") {

            it("UNAUTHENTICATED kode gir IrrecoverableRestException med UNAUTHORIZED status") {
                val ex = shouldThrow<IrrecoverableRestException> {
                    handler.handle(uri, fieldAccessException(responseError("UNAUTHENTICATED")))
                }
                ex.statusCode shouldBe UNAUTHORIZED
            }

            it("annen kode gir IrrecoverableRestException med tilsvarende status") {
                val ex = shouldThrow<IrrecoverableRestException> {
                    handler.handle(uri, fieldAccessException(responseError("FORBIDDEN")))
                }
                ex.statusCode shouldBe FORBIDDEN
            }

            it("ingen errors gir IrrecoverableRestException med INTERNAL_SERVER_ERROR status") {
                val ex = shouldThrow<IrrecoverableRestException> {
                    handler.handle(uri, fieldAccessException())
                }
                ex.statusCode shouldBe INTERNAL_SERVER_ERROR
            }
        }

        describe("GraphQlTransportException") {

            it("kaster RecoverableRestException med INTERNAL_SERVER_ERROR status") {
                val ex = shouldThrow<RecoverableRestException> {
                    handler.handle(uri, GraphQlTransportException("timeout", RuntimeException(), mockk()))
                }
                ex.statusCode shouldBe INTERNAL_SERVER_ERROR
            }
        }

        describe("annen exception") {

            it("kaster IrrecoverableRestException med INTERNAL_SERVER_ERROR status") {
                val ex = shouldThrow<IrrecoverableRestException> {
                    handler.handle(uri, RuntimeException("noe gikk galt"))
                }
                ex.statusCode shouldBe INTERNAL_SERVER_ERROR
            }
        }
    }
})

