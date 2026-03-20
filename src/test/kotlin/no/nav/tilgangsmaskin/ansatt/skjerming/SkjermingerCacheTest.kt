package no.nav.tilgangsmaskin.ansatt.skjerming

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldContainExactly
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.felles.rest.RetryLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.time.Duration
import java.time.Duration.ofSeconds

@RestClientTest(components = [SkjermingRestClientAdapter::class, SkjermingClientBeanConfig::class, SkjermingTjeneste::class, RetryLogger::class])
@EnableConfigurationProperties(SkjermingConfig::class)
@EnableResilientMethods
@TestPropertySource(properties = ["skjerming.base-uri=http://skjerming"])
@Import(SkjermingerCacheTest.CacheTestConfig::class)
@ApplyExtension(SpringExtension::class)
class SkjermingerCacheTest : BehaviorSpec() {

    @TestConfiguration
    @EnableCaching
    class CacheTestConfig {
        @Bean
        fun cacheManager(): CacheManager = ConcurrentMapCacheManager(SKJERMING)
        @Bean
        fun cache(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)
    }

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

        given("skjerminger") {
            `when`("noen identer er i cache") {
                then("Rest kalles kun for cache-misser, treff hentes fra cache") {
                    mockServer.expect(times(1), requestTo(cfg.skjermingerUri))
                        .andRespond(withSuccess("""{"$I2":true}""", APPLICATION_JSON))
                    putOne(ID1, false)
                    skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                    getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                }
            }

            `when`("alle identer er i cache") {
                then("Rest kalles ikke") {
                    putOne(ID1, false)
                    putOne(ID2, true)
                    mockServer.expect(never(), requestTo(cfg.skjermingerUri))
                    skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                }
            }

            `when`("et cache-innslag er slettet") {
                then("Rest kalles igjen for det slettede innslaget") {
                    putOne(ID1, false)
                    putOne(ID2, true)
                    cache.delete(SKJERMING_CACHE, I2)
                    mockServer.expect(times(1), requestTo(cfg.skjermingerUri))
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