package no.nav.tilgangsmaskin.felles.rest

import ch.qos.logback.classic.Level.DEBUG
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

class LoggingRequestInterceptorTest : DescribeSpec({

    val interceptor = LoggingRequestInterceptor()
    val builder = RestClient.builder().requestInterceptor(interceptor)
    lateinit var server: MockRestServiceServer
    lateinit var client: RestClient
    lateinit var logAppender: ListAppender<ILoggingEvent>

    beforeEach {
        server = bindTo(builder).build()
        client = builder.build()

        logAppender = ListAppender<ILoggingEvent>().also {
            it.start()
            (getLogger(LoggingRequestInterceptor::class.java) as Logger).apply {
                level = DEBUG
                addAppender(it)
            }
        }
    }

    afterEach {
        (getLogger(LoggingRequestInterceptor::class.java) as Logger).detachAppender(logAppender)
        server.verify()
    }

    describe("intercept") {

        it("logger ikke når URI inneholder 'monitoring'") {
            server.expect(requestTo("http://example.com/monitoring/health"))
                .andRespond(withSuccess())

            client.get().uri("http://example.com/monitoring/health")
                .retrieve().toBodilessEntity()

            logAppender.list.shouldBeEmpty()
        }

        it("logger ikke ved tom body og 2xx respons") {
            server.expect(requestTo("http://example.com/api/resource"))
                .andExpect(method(GET))
                .andRespond(withSuccess())

            client.get().uri("http://example.com/api/resource")
                .retrieve().toBodilessEntity()

            logAppender.list.shouldBeEmpty()
        }

        it("logger body når body ikke er tom") {
            server.expect(requestTo("http://example.com/api/resource"))
                .andExpect(method(POST))
                .andRespond(withSuccess("""{"result":"ok"}""", APPLICATION_JSON))

            client.post().uri("http://example.com/api/resource")
                .body("""{"key":"value"}""")
                .retrieve().toBodilessEntity()

            logAppender.list shouldHaveSize 1
            logAppender.list[0].formattedMessage shouldContain """{"key":"value"}"""
        }

        it("logger response status når respons ikke er 2xx") {
            server.expect(requestTo("http://example.com/api/resource"))
                .andExpect(method(GET))
                .andRespond(withStatus(INTERNAL_SERVER_ERROR))

            client.get().uri("http://example.com/api/resource")
                .retrieve().onStatus({ it.isError }, { _, _ -> })
                .toBodilessEntity()

            logAppender.list shouldHaveSize 1
            logAppender.list[0].formattedMessage shouldContain "500"
        }

        it("logger både body og response status når begge er til stede") {
            server.expect(requestTo("http://example.com/api/resource"))
                .andExpect(method(POST))
                .andRespond(withStatus(INTERNAL_SERVER_ERROR))

            client.post().uri("http://example.com/api/resource")
                .body("""{"key":"value"}""")
                .retrieve().onStatus({ it.isError }, { _, _ -> })
                .toBodilessEntity()

            logAppender.list shouldHaveSize 2
            logAppender.list.map { it.formattedMessage }.joinToString() shouldContain """{"key":"value"}"""
            logAppender.list.map { it.formattedMessage }.joinToString() shouldContain "500"
        }

        it("statusCode er tilgjengelig i responsen") {
            server.expect(requestTo("http://example.com/api/resource"))
                .andRespond(withSuccess())

            val resp = client.get().uri("http://example.com/api/resource")
                .retrieve().toBodilessEntity()

            resp.statusCode shouldBe OK
        }
    }
})
