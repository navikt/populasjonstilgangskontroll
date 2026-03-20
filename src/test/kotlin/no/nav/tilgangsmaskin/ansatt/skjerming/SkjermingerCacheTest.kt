package no.nav.tilgangsmaskin.ansatt.skjerming

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldContainExactly
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheTest
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class SkjermingerCacheTest : AbstractCacheTest() {

    @Autowired
    private lateinit var mapper: JsonMapper

    private lateinit var skjerming: SkjermingTjeneste
    private lateinit var mockServer: MockRestServiceServer

    override fun cacheConfigurations() = mapOf(
        SKJERMING_CACHE.name to defaultCacheConfig()
            .prefixCacheNameWith(SKJERMING)
            .disableCachingNullValues()
    )

    init {
        beforeEach {
            setUpCache()
            val restClientBuilder = RestClient.builder().baseUrl("${cfg.baseUri}")
            mockServer = bindTo(restClientBuilder).build()
            skjerming = SkjermingTjeneste(SkjermingRestClientAdapter(restClientBuilder.build(), cfg), cache, cfg)
            IDS.forEach { cache.delete(SKJERMING_CACHE, it) }
        }

        describe("skjerminger") {

            it("Rest kalles kun for cache-misser, treff hentes fra cache") {
                putOne(ID1, false)
                mockServer.expect(requestTo(cfg.skjermingerUri))
                    .andRespond(withSuccess(mapper.writeValueAsString(mapOf(I2 to true)),
                        APPLICATION_JSON))

                skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                mockServer.verify()
            }

            it("Rest kalles ikke når alle er i cache") {
                putOne(ID1, false)
                putOne(ID2, true)
                skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                mockServer.verify()
            }

            it("Lytteren publiserer en CacheInnslagFjernetEvent når en nøkkel utløper") {
                val mottatt = mutableListOf<CacheInnslagFjernetEvent>()
                ctx.addApplicationListener(ApplicationListener<CacheInnslagFjernetEvent> {
                    mottatt.add(it)
                })
                putOne(ID1, false)
                eventually(3.seconds) {
                    mottatt.isNotEmpty()
                }
            }

            it("Rest kalles igjen etter at et cache-innslag er slettet") {
                putOne(ID1, false)
                putOne(ID2, true)
                cache.delete(SKJERMING_CACHE, I2)
                mockServer.expect(requestTo(cfg.skjermingerUri))
                    .andRespond(withSuccess(mapper.writeValueAsString(mapOf(I2 to true)),
                        APPLICATION_JSON))

                skjerming.skjerminger(listOf(ID1, ID2)) shouldContainExactly mapOf(ID1 to false, ID2 to true)
                getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                mockServer.verify()
            }
        }
    }

    private fun putOne(brukerId: BrukerId, skjermet: Boolean, duration: Duration = Duration.ofSeconds(1)) =
        cache.putOne(SKJERMING_CACHE, brukerId.verdi, skjermet, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(SKJERMING_CACHE, ids, Boolean::class)

    private companion object {
        private val cfg = SkjermingConfig(URI.create("http://skjerming"))
    }
}