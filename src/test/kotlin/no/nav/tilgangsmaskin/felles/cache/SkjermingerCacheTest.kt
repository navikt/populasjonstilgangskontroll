package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.SECONDS

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

    @BeforeEach
    fun setUp() {
        val restClientBuilder = RestClient.builder().baseUrl("${cfg.baseUri}")
        mockServer = bindTo(restClientBuilder).build()
        skjerming = SkjermingTjeneste(SkjermingRestClientAdapter(restClientBuilder.build(), cfg), cache, cfg)
        IDS.forEach { cache.delete(SKJERMING_CACHE, it) }
    }

    @Test
    @DisplayName("Rest kalles kun for cache-misser, treff hentes fra cache")
    fun restKallesKunForCacheMisser() {
        putOne(ID1, false)
        mockServer.expect(requestTo(cfg.skjermingerUri))
            .andRespond(withSuccess(mapper.writeValueAsString(mapOf(I2 to true)), APPLICATION_JSON))

        assertThat(skjerming.skjerminger(listOf(ID1, ID2))).containsExactlyInAnyOrderEntriesOf(mapOf(ID1 to false, ID2 to true))
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        mockServer.verify()
    }

    @Test
    @DisplayName("Rest kalles ikke når alle er i cache")
    fun restKallesIkkeNårAlleErICache() {
        putOne(ID1, false)
        putOne(ID2, true)
        assertThat(skjerming.skjerminger(listOf(ID1, ID2))).containsExactlyInAnyOrderEntriesOf(mapOf(ID1 to false, ID2 to true))
        mockServer.verify()
    }

    @Test
    @DisplayName("Verifiser at lytteren publiserer en CacheInnslagFjernetEvent når en nøkkel utløper")
    fun listenerPublisererEventVedUtløp() {
        val mottatt = mutableListOf<CacheInnslagFjernetEvent>()
        ctx.addApplicationListener(ApplicationListener<CacheInnslagFjernetEvent> { mottatt.add(it) })
        putOne(ID1, false)
        await.atMost(3, SECONDS).until {
            mottatt.isNotEmpty()
        }
    }

    @Test
    @DisplayName("Rest kalles igjen etter at en cache-innslag er slettet")
    fun restKallesIgjenEtterSlettingAvCacheInngang() {
        putOne(ID1, false)
        putOne(ID2, true)
        cache.delete(SKJERMING_CACHE, I2)
        mockServer.expect(requestTo(cfg.skjermingerUri))
            .andRespond(withSuccess(mapper.writeValueAsString(mapOf(I2 to true)), APPLICATION_JSON))

        assertThat(skjerming.skjerminger(listOf(ID1, ID2))).containsExactlyInAnyOrderEntriesOf(mapOf(ID1 to false, ID2 to true))
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        mockServer.verify()
    }

    private fun putOne(brukerId: BrukerId, skjermet: Boolean, duration: Duration = ofSeconds(1)) =
        cache.putOne(SKJERMING_CACHE, brukerId.verdi, skjermet, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(SKJERMING_CACHE, ids, Boolean::class)

    companion object {
        private val cfg = SkjermingConfig(URI.create("http://skjerming"))
    }
}
