package no.nav.tilgangsmaskin.ansatt.skjerming

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Level.WARN
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RetryLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(components = [SkjermingRestClientAdapter::class, SkjermingClientBeanConfig::class, SkjermingTjeneste::class, RetryLogger::class])
@EnableConfigurationProperties(SkjermingConfig::class)
@EnableResilientMethods
@TestPropertySource(properties = ["skjerming.base-uri=http://skjerming"])
@ApplyExtension(SpringExtension::class)
class SkjermingRetryTest : DescribeSpec() {

    @Autowired lateinit var tjeneste: SkjermingTjeneste
    @Autowired lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: SkjermingConfig

    @MockkBean lateinit var cache: CacheOperations

    init {
        val brukerId = BrukerId("08526835670")

        beforeEach { server.reset() }

        describe("skjerming") {

            it("prøver 4 ganger og kaster RecoverableRestException når alle forsøk feiler") {
                server.expect(times(4), requestTo(cfg.skjermingUri))
                    .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                shouldThrow<RecoverableRestException> {
                    tjeneste.skjerming(brukerId)
                }

                server.verify()
            }

            it("returnerer resultat etter retry når andre forsøk lykkes") {
                server.expect(times(1), requestTo(cfg.skjermingUri))
                    .andRespond(withStatus(INTERNAL_SERVER_ERROR))
                server.expect(times(1), requestTo(cfg.skjermingUri))
                    .andRespond(withSuccess("false", APPLICATION_JSON))

                tjeneste.skjerming(brukerId) shouldBe false

                server.verify()
            }

            it("kaster IrrecoverableRestException uten retry") {
                server.expect(times(1), requestTo(cfg.skjermingUri))
                    .andRespond(withStatus(NOT_FOUND))

                shouldThrow<IrrecoverableRestException> {
                    tjeneste.skjerming(brukerId)
                }

                server.verify()
            }
        }

        describe("RetryLogger") {

            fun withLogCapture(block: (ListAppender<ILoggingEvent>) -> Unit): List<ILoggingEvent> {
                val logger = LoggerFactory.getLogger(RetryLogger::class.java) as Logger
                val appender = ListAppender<ILoggingEvent>().also { it.start() }
                logger.addAppender(appender)
                try {
                    block(appender)
                } finally {
                    logger.detachAppender(appender)
                }
                return appender.list
            }

            it("logger WARN for hvert retry-forsøk ved RecoverableRestException") {
                server.expect(times(4), requestTo(cfg.skjermingUri))
                    .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                val logs = withLogCapture {
                    shouldThrow<RecoverableRestException> { tjeneste.skjerming(brukerId) }
                }

                logs.any { e -> e.level == WARN && e.formattedMessage.contains("skjerming") } shouldBe true
                server.verify()
            }

            it("logger WARN abort når alle retry-forsøk er brukt opp") {
                server.expect(times(4), requestTo(cfg.skjermingUri))
                    .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                val logs = withLogCapture {
                    shouldThrow<RecoverableRestException> { tjeneste.skjerming(brukerId) }
                }

                logs.filter { e -> e.level == WARN }.last().formattedMessage shouldContain "skjerming"
                server.verify()
            }

            it("logger INFO for NotFoundRestException uten retry") {
                server.expect(times(1), requestTo(cfg.skjermingUri))
                    .andRespond(withStatus(NOT_FOUND))

                val logs = withLogCapture {
                    shouldThrow<IrrecoverableRestException> { tjeneste.skjerming(brukerId) }
                }

                logs.any { e -> e.level == INFO && e.formattedMessage.contains("skjerming") } shouldBe true
                server.verify()
            }
        }
    }
}
