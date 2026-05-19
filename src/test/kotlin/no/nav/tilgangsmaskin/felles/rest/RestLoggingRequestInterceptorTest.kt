package no.nav.tilgangsmaskin.felles.rest

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import java.net.URI

class RestLoggingRequestInterceptorTest : BehaviorSpec({

    val interceptor = RestLoggingRequestInterceptor()
    val execution = mockk<ClientHttpRequestExecution>()

    fun withLogCapture(level: Level = TRACE, block: () -> Unit): List<ILoggingEvent> {
        val logger = getLogger(RestLoggingRequestInterceptor::class.java) as Logger
        val originalLevel = logger.level
        logger.level = level
        val appender = ListAppender<ILoggingEvent>().apply { start(); logger.addAppender(this) }
        return try {
            block()
            appender.list.toList()
        } finally {
            logger.detachAppender(appender)
            logger.level = originalLevel
        }
    }

    Given("en vanlig request med body") {
        val request = MockClientHttpRequest(HttpMethod.POST, URI.create("https://api.nav.no/api/vedtak/123"))
        val body = """{"felt": "verdi"}""".toByteArray()
        val response = MockClientHttpResponse(ByteArray(0), HttpStatusCode.valueOf(200))

        every { execution.execute(request, body) } returns response

        When("intercept kalles") {
            Then("logger body og respons-status") {
                val events = withLogCapture {
                    interceptor.intercept(request, body, execution)
                }

                events shouldHaveSize 2
                events[0].formattedMessage shouldContain """{"felt": "verdi"}"""
                events[0].formattedMessage shouldContain "POST"
                events[1].formattedMessage shouldContain "200"
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
            Then("logger kun respons-status, ikke body") {
                val events = withLogCapture {
                    interceptor.intercept(request, body, execution)
                }

                events shouldHaveSize 1
                events[0].formattedMessage shouldContain "200"
                events[0].formattedMessage shouldContain "GET"
            }
        }
    }

    Given("en monitoring-request") {
        val request = MockClientHttpRequest(HttpMethod.GET, URI.create("https://api.nav.no/monitoring/health"))
        val body = ByteArray(0)
        val response = MockClientHttpResponse(ByteArray(0), HttpStatusCode.valueOf(200))

        every { execution.execute(request, body) } returns response

        When("intercept kalles") {
            Then("logger ingenting") {
                val events = withLogCapture {
                    interceptor.intercept(request, body, execution)
                }
                events.shouldBeEmpty()
            }

            Then("returnerer responsen") {
                val result = interceptor.intercept(request, body, execution)
                result shouldBe response
            }
        }
    }

    Given("execution kaster exception") {
        val request = MockClientHttpRequest(HttpMethod.GET, URI.create("https://api.nav.no/api/feil"))
        val body = ByteArray(0)
        every { execution.execute(request, body) } throws RuntimeException("Connection refused")
        When("intercept kalles") {
            Then("exception propageres og respons-status logges ikke") {
                val events = withLogCapture {
                    runCatching {
                        interceptor.intercept(request, body, execution)
                    }
                }
                events.shouldBeEmpty()
            }
        }
    }
})
