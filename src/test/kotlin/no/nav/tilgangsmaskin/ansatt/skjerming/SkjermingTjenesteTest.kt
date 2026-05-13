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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_BULK_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_BASE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RestRetryLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.util.UriComponentsBuilder.fromUriString

@RestClientTest(components = [SkjermingClient::class,SkjermingClientBeanConfig::class, SkjermingTjeneste::class, SkjermingConfig::class,RestRetryLogger::class])
@EnableResilientMethods
@Import(SkjermingTjenesteTest.CacheConfig::class)
@ApplyExtension(SpringExtension::class)
class SkjermingTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableCaching
    class CacheConfig {
        @Bean
        fun cacheManager(): CacheManager =
            ConcurrentMapCacheManager(SKJERMING)
        @Bean
        fun cache(cacheManager: CacheManager) = ConcurrentMapCacheOperations(cacheManager)
    }

    @Autowired
    lateinit var tjeneste: SkjermingTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired
    lateinit var cfg: SkjermingConfig
    @Autowired
    lateinit var cacheManager: CacheManager
    @Autowired
    lateinit var cache: CacheOperations

    init {
        val brukerId = BrukerId("08526835670")
        val brukerId2 = BrukerId("20478606614")

        fun withLogCapture(block: (ListAppender<ILoggingEvent>) -> Unit): List<ILoggingEvent> {
            val logger = LoggerFactory.getLogger(RestRetryLogger::class.java) as Logger
            val appender = ListAppender<ILoggingEvent>().apply { start(); (logger).addAppender(this) }
            return try { block(appender); appender.list } finally { logger.detachAppender(appender) }
        }

        beforeEach {
            server.reset()
            cacheManager.getCache(SKJERMING)?.clear()
        }

        Given("cache - skjerming (@Cacheable)") {
            When("samme brukerId slås opp to ganger") {
                Then("andre kall returneres fra cache uten REST-kall") {
                    server.expect(times(1), requestTo(SKJERMING_URI))
                        .andRespond(withSuccess("true", APPLICATION_JSON))
                    tjeneste.skjerming(brukerId) shouldBe true
                    tjeneste.skjerming(brukerId) shouldBe true
                    cache.getOne(SKJERMING_CACHE, brukerId.verdi, Boolean::class) shouldBe true
                    server.verify()
                }
            }
            When("to ulike brukerId-er slås opp") {
                Then("caches de separat") {
                    server.expect(requestTo(SKJERMING_URI)).andRespond(withSuccess("true", APPLICATION_JSON))
                    server.expect(requestTo(SKJERMING_URI)).andRespond(withSuccess("false", APPLICATION_JSON))
                    tjeneste.skjerming(brukerId) shouldBe true
                    tjeneste.skjerming(brukerId2) shouldBe false
                    cache.getOne(SKJERMING_CACHE, brukerId.verdi, Boolean::class) shouldBe true
                    cache.getOne(SKJERMING_CACHE, brukerId2.verdi, Boolean::class) shouldBe false
                    server.verify()
                }
            }
        }

        Given("cache - skjerminger (CacheOperations)") {
            When("én er i cache og én er cache-miss") {
                Then("hentes treff fra cache og kun misser fra REST") {
                    cache.putOne(SKJERMING_CACHE, brukerId.verdi, true, cfg.varighet)
                    server.expect(times(1), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"${brukerId2.verdi}":false}""", APPLICATION_JSON))
                    val result = tjeneste.skjerminger(listOf(brukerId, brukerId2))
                    result[brukerId] shouldBe true
                    result[brukerId2] shouldBe false
                    server.verify()
                }
            }
            When("alle er i cache") {
                Then("kalles ikke REST") {
                    cache.putOne(SKJERMING_CACHE, brukerId.verdi, true, cfg.varighet)
                    cache.putOne(SKJERMING_CACHE, brukerId2.verdi, false, cfg.varighet)
                    val result = tjeneste.skjerminger(listOf(brukerId, brukerId2))
                    result[brukerId] shouldBe true
                    result[brukerId2] shouldBe false
                    server.verify()
                }
            }
            When("ingen identer") {
                Then("kalles ikke REST") {
                    server.expect(never(), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("{}", APPLICATION_JSON))
                    tjeneste.skjerminger(emptyList()) shouldBe emptyMap()
                    server.verify()
                }
            }
            When("REST returnerer resultat") {
                Then("lagres resultater i cache") {
                    server.expect(times(1), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"${brukerId.verdi}":true}""", APPLICATION_JSON))
                    tjeneste.skjerminger(listOf(brukerId))
                    cache.getOne(SKJERMING_CACHE, brukerId.verdi, Boolean::class) shouldBe true
                    server.verify()
                }
            }
        }

        Given("retry") {
            When("alle 4 forsøk feiler med 500") {
                Then("kastes RecoverableRestException") {
                    server.expect(times(4), requestTo(SKJERMING_URI)).andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    shouldThrow<RecoverableRestException> { tjeneste.skjerming(brukerId) }
                    server.verify()
                }
            }
            When("første forsøk feiler og andre lykkes") {
                Then("returneres resultat fra andre forsøk") {
                    server.expect(times(1), requestTo(SKJERMING_URI)).andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    server.expect(times(1), requestTo(SKJERMING_URI)).andRespond(withSuccess("false", APPLICATION_JSON))
                    tjeneste.skjerming(brukerId) shouldBe false
                    server.verify()
                }
            }
            When("tjenesten returnerer 404") {
                Then("kastes IrrecoverableRestException uten retry") {
                    server.expect(times(1), requestTo(SKJERMING_URI)).andRespond(withStatus(NOT_FOUND))
                    shouldThrow<IrrecoverableRestException> { tjeneste.skjerming(brukerId) }
                    server.verify()
                }
            }
        }

        Given("config") {
            When("config leses") {
                Then("navn er korrekt") {
                    cfg.navn shouldBe cfg.name
                    cfg.caches shouldBe setOf(SKJERMING_CACHE)
                }
            }
        }

        Given("RetryLogger") {
            When("RecoverableRestException kastes etter retry") {
                Then("logges WARN for hvert retry-forsøk") {
                    server.expect(times(4), requestTo(SKJERMING_URI)).andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    val logs = withLogCapture { shouldThrow<RecoverableRestException> { tjeneste.skjerming(brukerId) } }
                    logs.any { e -> e.level == WARN && e.formattedMessage.contains("skjerming") } shouldBe true
                    server.verify()
                }
                Then("siste logg inneholder tjenestenavn") {
                    server.expect(times(4), requestTo(SKJERMING_URI)).andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    val logs = withLogCapture { shouldThrow<RecoverableRestException> { tjeneste.skjerming(brukerId) } }
                    logs.last { e -> e.level == WARN }.formattedMessage shouldContain "skjerming"
                    server.verify()
                }
            }
            When("IrrecoverableRestException kastes uten retry") {
                Then("logges INFO") {
                    server.expect(times(1), requestTo(SKJERMING_URI)).andRespond(withStatus(NOT_FOUND))
                    val logs = withLogCapture { shouldThrow<IrrecoverableRestException> { tjeneste.skjerming(brukerId) } }
                    logs.any { e -> e.level == INFO && e.formattedMessage.contains("skjerming") } shouldBe true
                    server.verify()
                }
            }
        }
    }
    companion object {
        val SKJERMING_URI = uri(SKJERMING_PATH)
        val SKJERMINGER_URI = uri(SKJERMING_BULK_PATH)
        private fun uri(path: String) =  fromUriString("$SKJERMING_BASE$path").build().toUri()
    }
}
