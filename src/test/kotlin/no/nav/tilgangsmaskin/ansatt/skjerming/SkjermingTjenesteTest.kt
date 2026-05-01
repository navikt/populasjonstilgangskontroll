package no.nav.tilgangsmaskin.ansatt.skjerming

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Level.WARN
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_BULK_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_BASE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RetryLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.util.UriComponentsBuilder.fromUriString
import java.time.Duration.ofSeconds

@RestClientTest(components = [SkjermingClient::class, SkjermingConfig::class, SkjermingClientBeanConfig::class, SkjermingTjeneste::class, RetryLogger::class])
@Import(SkjermingTestConfig::class)
@ApplyExtension(SpringExtension::class)
class SkjermingTjenesteTest : BehaviorSpec() {

    @Autowired
    lateinit var tjeneste: SkjermingTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired
    lateinit var cfg: SkjermingConfig
    @Autowired
    lateinit var cache: CacheOperations

    init {
        beforeEach {
            server.reset()
            delete(ID1,ID2)
        }
        afterEach { server.verify() }

        Given("skjerming (@Cacheable)") {
            When("samme brukerId kalles to ganger") {
                Then("andre kall returneres fra cache — REST kalles ikke på nytt") {
                    server.expect(requestTo(SKJERMING_URI))
                        .andRespond(withSuccess("true", APPLICATION_JSON))

                    tjeneste.skjerming(ID1) shouldBe true
                    tjeneste.skjerming(ID1) shouldBe true
                    cache.getOne(SKJERMING_CACHE, ID1.verdi, Boolean::class) shouldBe true
                }
            }

            When("ulike brukerId-er") {
                Then("cachet separat") {
                    server.expect(requestTo(SKJERMING_URI))
                        .andRespond(withSuccess("true", APPLICATION_JSON))
                    server.expect(requestTo(SKJERMING_URI))
                        .andRespond(withSuccess("false", APPLICATION_JSON))

                    tjeneste.skjerming(ID1) shouldBe true
                    tjeneste.skjerming(ID2) shouldBe false
                    cache.getOne(SKJERMING_CACHE, ID1.verdi, Boolean::class) shouldBe true
                    cache.getOne(SKJERMING_CACHE, ID2.verdi, Boolean::class) shouldBe false
                }
            }
        }

        Given("skjerminger (CacheOperations)") {
            When("noen identer er i cache") {
                Then("henter treff fra cache og kun misser fra REST") {
                    putOne(ID1, true)
                    server.expect(requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"${ID2.verdi}":false}""", APPLICATION_JSON))

                    skjerminger(ID1, ID2) shouldContainExactly mapOf(ID1 to true, ID2 to false)
                    getMany(ID1, ID2).keys shouldContainExactlyInAnyOrder setOf(ID1.verdi, ID2.verdi)
                }
            }

            When("alle identer er i cache") {
                Then("REST kalles ikke") {
                    putOne(ID1, true)
                    putOne(ID2, false)
                    server.expect(never(), requestTo(SKJERMINGER_URI))

                    skjerminger(ID1, ID2) shouldContainExactly mapOf(ID1 to true, ID2 to false)
                }
            }

            When("et cache-innslag er slettet") {
                Then("REST kalles igjen for det slettede innslaget") {
                    putOne(ID1, false)
                    putOne(ID2, true)
                    cache.delete(SKJERMING_CACHE, I2)
                    server.expect(requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"${ID2.verdi}":true}""", APPLICATION_JSON))

                    skjerminger(ID1, ID2) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                    getMany(ID1, ID2).keys shouldContainExactlyInAnyOrder setOf(ID1.verdi, ID2.verdi)
                }
            }

            When("ingen identer") {
                Then("REST kalles ikke") {
                    server.expect(never(), requestTo(SKJERMINGER_URI))
                    tjeneste.skjerminger(emptyList()) shouldBe emptyMap()
                }
            }

            When("REST-kall lykkes") {
                Then("lagrer resultater i cache") {
                    server.expect(requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"${ID1.verdi}":true}""", APPLICATION_JSON))

                    tjeneste.skjerminger(listOf(ID1))
                    cache.getOne(SKJERMING_CACHE, ID1.verdi, Boolean::class) shouldBe true
                }
            }
        }

        Given("retry-mekanisme") {
            When("alle forsøk feiler med 500") {
                Then("prøver 4 ganger og kaster RecoverableRestException") {
                    server.expect(times(4), requestTo(SKJERMING_URI))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    shouldThrow<RecoverableRestException> { tjeneste.skjerming(ID1) }
                }
            }

            When("andre forsøk lykkes") {
                Then("returnerer resultat etter retry") {
                    server.expect(times(1), requestTo(SKJERMING_URI))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    server.expect(times(1), requestTo(SKJERMING_URI))
                        .andRespond(withSuccess("false", APPLICATION_JSON))

                    tjeneste.skjerming(ID1) shouldBe false
                }
            }

            When("tjenesten returnerer 404") {
                Then("kaster IrrecoverableRestException uten retry") {
                    server.expect(times(1), requestTo(SKJERMING_URI))
                        .andRespond(withStatus(NOT_FOUND))

                    shouldThrow<IrrecoverableRestException> { tjeneste.skjerming(ID1) }
                }
            }
        }

        Given("RetryLogger") {
            When("RecoverableRestException kastes") {
                Then("logger WARN for hvert retry-forsøk") {
                    server.expect(times(4), requestTo(SKJERMING_URI))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    val logs = withLogCapture { shouldThrow<RecoverableRestException> { tjeneste.skjerming(ID1) } }
                    logs.any { it.level == WARN && it.formattedMessage.contains("skjerming") } shouldBe true
                }

                Then("logger WARN abort når alle forsøk er brukt opp") {
                    server.expect(times(4), requestTo(SKJERMING_URI))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    val logs = withLogCapture { shouldThrow<RecoverableRestException> { tjeneste.skjerming(ID1) } }
                    logs.last { it.level == WARN }.formattedMessage shouldContain "skjerming"
                }
            }

            When("IrrecoverableRestException kastes") {
                Then("logger INFO uten retry") {
                    server.expect(times(1), requestTo(SKJERMING_URI))
                        .andRespond(withStatus(NOT_FOUND))

                    val logs = withLogCapture { shouldThrow<IrrecoverableRestException> { tjeneste.skjerming(ID1) } }
                    logs.any { it.level == INFO && it.formattedMessage.contains("skjerming") } shouldBe true
                }
            }
        }
    }

    private fun delete(vararg ids: BrukerId) =
        ids.forEach { cache.delete(SKJERMING_CACHE, it.verdi) }

    private fun putOne(brukerId: BrukerId, skjermet: Boolean) =
        cache.putOne(SKJERMING_CACHE, brukerId.verdi, skjermet, ofSeconds(5))

    private fun skjerminger(vararg ids: BrukerId) = tjeneste.skjerminger(ids.toList())

    private fun getMany(vararg ids: BrukerId) = cache.getMany(SKJERMING_CACHE, ids.map { it.verdi }.toSet(), Boolean::class)

    private fun withLogCapture(block: () -> Unit): List<ILoggingEvent> {
        val logger = LoggerFactory.getLogger(RetryLogger::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>().apply { start(); logger.addAppender(this) }
        block()
        logger.detachAppender(appender)
        return appender.list
    }

    companion object {
        private const val I1 = "08526835670"
        private const val I2 = "20478606614"
        private val ID1 = BrukerId(I1)
        private val ID2 = BrukerId(I2)
        val SKJERMING_URI = uri(SKJERMING_PATH)
        val SKJERMINGER_URI = uri(SKJERMING_BULK_PATH)
        private fun uri(path: String) = fromUriString("$SKJERMING_BASE$path").build().toUri()
    }
}
