package no.nav.tilgangsmaskin.bruker.pdl

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.BrukerTilPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlTestMapper.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlTestMapper.restRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjenesteTest.PdlTestConfig
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import java.time.Duration.ofSeconds

@RestClientTest(components = [PdlRestClientAdapter::class, PdlTjeneste::class])
@EnableConfigurationProperties(PdlConfig::class)
@Import(PdlTestConfig::class)
@TestPropertySource(properties = ["pdl.base-uri=http://pdl"])
@EnableResilientMethods
@ApplyExtension(SpringExtension::class)
class PdlTjenesteTest : DescribeSpec() {

    @TestConfiguration
    @EnableCaching
    class PdlTestConfig {
        @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager(PDL)
        @Bean fun cache(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)

        @Bean
        @Qualifier(PDL)
        fun pdlRestClient(b: RestClient.Builder) = b.build()
    }

    @MockkBean lateinit var graphQL: PdlSyncGraphQLClientAdapter

    @Autowired lateinit var pdl: PdlTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: PdlConfig
    @Autowired lateinit var cacheManager: CacheManager
    @Autowired lateinit var cache: CacheOperations
    @Autowired lateinit var mapper: JsonMapper

    init {
        beforeEach {
            server.reset()
            cacheManager.getCache(PDL)?.clear()
            every { graphQL.partnere(any()) } returns emptySet()
        }

        describe("person") {
            it("hentPerson kaller REST og oppdaterer cache") {
                server.expect(requestTo(cfg.personURI))
                    .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons( P1)), APPLICATION_JSON))
                pdl.medFamilie(I1) shouldBe P1
                server.verify()
                cache.getOne(PDL_MED_FAMILIE_CACHE, I1, Person::class) shouldBe P1
            }
        }

        describe("personer") {

            it("REST kalles kun for cache-misser, treff hentes fra cache") {
                cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                server.expect(requestTo(cfg.personerURI))
                    .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                server.verify()
            }

            it("REST kalles ikke når alle er i cache") {
                cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                cache.putOne(PDL_MED_FAMILIE_CACHE, I2, P2, ofSeconds(2))
                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                server.verify()
            }

            it("slett ett element og verifiser at REST kalles for det elementet") {
                cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                cache.putOne(PDL_MED_FAMILIE_CACHE, I2, P2, ofSeconds(2))
                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                cache.delete(PDL_MED_FAMILIE_CACHE, I2)
                server.expect(requestTo(cfg.personerURI))
                    .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))

                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                cache.getOne(PDL_MED_FAMILIE_CACHE, I2, Person::class) shouldBe P2
                server.verify()
            }

            it("REST kalles ikke når settet er tomt") {
                pdl.personer(emptySet()) shouldContainExactlyInAnyOrder emptyList()
                server.verify()
            }
        }
    }

    companion object {
        const val I1 = "03508331575"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val P1 = tilPerson(BrukerBuilder(BrukerId(I1))
            .aktørId(AktørId("1234567890123"))
            .gt(KommuneTilknytning(Kommune("0301")))
            .build())
        val P2 = tilPerson(BrukerBuilder(BrukerId(I2))
            .aktørId(AktørId("9876543210987"))
            .gt(UtenlandskTilknytning())
            .build())
    }
}