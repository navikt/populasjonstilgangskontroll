package no.nav.tilgangsmaskin.ansatt.skjerming

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldContainExactly
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjenesteTest.Companion.SKJERMINGER_URI
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.RetryLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.time.Duration
import java.time.Duration.ofSeconds

@RestClientTest(components = [SkjermingPingable::class, SkjermingClientBeanConfig::class,SkjermingConfig::class, SkjermingClient::class,SkjermingTjeneste::class, RetryLogger::class])
@EnableResilientMethods
@Import(CacheConfig::class)
@ApplyExtension(SpringExtension::class)
class SkjermingerCacheTest : BehaviorSpec() {


    @Autowired
    private lateinit var skjerming: SkjermingTjeneste
    @Autowired
    private lateinit var mockServer: MockRestServiceServer
    @Autowired
    private lateinit var cfg: SkjermingConfig
    @Autowired
    private lateinit var cache: CacheOperations
    @Autowired
    private lateinit var cacheManager: CacheManager

    init {
        beforeEach {
            cacheManager.getCache(SKJERMING)?.clear()
        }
        afterEach {
            mockServer.verify()
        }

        Given("skjerminger") {
            When("noen identer er i cache") {
                Then("Rest kalles kun for cache-misser, treff hentes fra cache") {
                    mockServer.expect(times(1), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"$I2":true}""", APPLICATION_JSON))
                    putOne(ID1, false)
                    skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                    getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                }
            }

            When("alle identer er i cache") {
                Then("Rest kalles ikke") {
                    putOne(ID1, false)
                    putOne(ID2, true)
                    mockServer.expect(never(), requestTo(SKJERMINGER_URI))
                    skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                }
            }

            When("et cache-innslag er slettet") {
                Then("Rest kalles igjen for det slettede innslaget") {
                    putOne(ID1, false)
                    putOne(ID2, true)
                    cache.delete(SKJERMING_CACHE, I2)
                    mockServer.expect(times(1), requestTo(SKJERMINGER_URI))
                        .andRespond(withSuccess("""{"$I2":true}""", APPLICATION_JSON))

                    skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                    getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                }
            }
        }
    }

    private fun putOne(brukerId: BrukerId, skjermet: Boolean, duration: Duration = ofSeconds(1)) =
        cache.putOne(SKJERMING_CACHE, brukerId.verdi, skjermet, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(SKJERMING_CACHE, ids, Boolean::class)

    private companion object {
        const val I1 = "03508331575"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val ID1 = BrukerId(I1)
        val ID2 = BrukerId(I2)
    }
}