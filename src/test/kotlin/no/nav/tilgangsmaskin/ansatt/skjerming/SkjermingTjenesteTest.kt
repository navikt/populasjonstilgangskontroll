package no.nav.tilgangsmaskin.ansatt.skjerming

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_BULK_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_BASE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjenesteTest.SkjermingTestConfig
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.CacheTestConfig
import no.nav.tilgangsmaskin.felles.cache.*
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RestRetryLogger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.util.UriComponentsBuilder.fromUriString

@RestClientTest(components = [SkjermingClient::class,SkjermingBeanConfig::class, SkjermingTjeneste::class, SkjermingConfig::class])
@Import(SkjermingTestConfig::class)
@ApplyExtension(SpringExtension::class)
class SkjermingTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    class SkjermingTestConfig : CacheTestConfig(SKJERMING)

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

        beforeEach {
            server.reset()
            cacheManager.getCache(SKJERMING)?.clear()
        }

        afterEach { server.verify() }

        Given("cache - skjerming (@Cacheable)") {
            When("samme brukerId slås opp to ganger") {
                Then("andre kall returneres fra cache uten REST-kall") {
                    server.expect(once(), requestTo(SKJERMING_URI))
                        .andRespond(withSuccess("true", APPLICATION_JSON))
                    tjeneste.skjerming(ID1).shouldBeTrue()
                    tjeneste.skjerming(ID1).shouldBeTrue()
                    cache.getOne<Boolean>(SKJERMING_CACHE, I1).shouldBeTrue()
                }
            }
        }

        Given("cache - skjerminger (CacheOperations)") {
            When("én er i cache og én er cache-miss") {
                Then("hentes treff fra cache og kun misser fra REST") {
                    cache.putOne(SKJERMING_CACHE, I1, true, cfg.varighet)
                    server.expect(once(), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"$I2":false}""", APPLICATION_JSON))
                    tjeneste.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to true, ID2 to false)
                    cache.getMany<Boolean>(SKJERMING_CACHE, IDS).keys shouldContainExactlyInAnyOrder IDS
                }
            }
            When("alle er i cache") {
                Then("kalles ikke REST") {
                    cache.putMany(SKJERMING_CACHE, mapOf(I1 to true, I2 to false), cfg.varighet)
                    server.expect(never(), requestTo(SKJERMINGER_URI))
                    tjeneste.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to true, ID2 to false)
                }
            }
            When("et cache-innslag er slettet") {
                Then("REST kalles igjen for det slettede innslaget") {
                    cache.putMany(SKJERMING_CACHE, mapOf(I1 to true, I2 to false), cfg.varighet)
                    cache.delete(SKJERMING_CACHE, I2)
                    server.expect(once(), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"$I2":false}""", APPLICATION_JSON))
                    tjeneste.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to true, ID2 to false)
                    cache.getMany<Boolean>(SKJERMING_CACHE, IDS).keys shouldContainExactlyInAnyOrder IDS
                }
            }
            When("ingen identer") {
                Then("kalles ikke REST") {
                    server.expect(never(), requestTo(SKJERMINGER_URI))
                    tjeneste.skjerminger(emptyList()).shouldBeEmpty()
                }
            }
            When("REST returnerer resultat") {
                Then("lagres resultater i cache") {
                    server.expect(once(), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"$I1":true}""", APPLICATION_JSON))
                    tjeneste.skjerminger(listOf(ID1))
                    cache.getOne<Boolean>(SKJERMING_CACHE, I1).shouldBeTrue()
                }
            }
        }

        Given("retry") {
            When("alle 4 forsøk feiler med 500") {
                Then("kastes RecoverableRestException") {
                    server.expect(times(4), requestTo(SKJERMING_URI)).andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    shouldThrow<RecoverableRestException> { tjeneste.skjerming(ID1) }
                }
            }
            When("første forsøk feiler og andre lykkes") {
                Then("returneres resultat fra andre forsøk") {
                    server.expect(once(), requestTo(SKJERMING_URI)).andRespond(withStatus(INTERNAL_SERVER_ERROR))
                    server.expect(once(), requestTo(SKJERMING_URI)).andRespond(withSuccess("false", APPLICATION_JSON))
                    tjeneste.skjerming(ID1) shouldBe false
                }
            }
            When("tjenesten returnerer 404") {
                Then("kastes IrrecoverableRestException uten retry") {
                    server.expect(once(), requestTo(SKJERMING_URI)).andRespond(withStatus(NOT_FOUND))
                    shouldThrow<IrrecoverableRestException> { tjeneste.skjerming(ID1) }
                }
            }
        }
    }

    companion object {
        const val I1 = "08526835670"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val ID1 = BrukerId(I1)
        val ID2 = BrukerId(I2)
        val SKJERMING_URI = uri(SKJERMING_PATH)
        val SKJERMINGER_URI = uri(SKJERMING_BULK_PATH)
        private fun uri(path: String) = fromUriString("$SKJERMING_BASE$path").build().toUri()
    }
}
