package no.nav.tilgangsmaskin.felles.cache

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import org.springframework.data.redis.cache.RedisCacheConfiguration
import tools.jackson.module.kotlin.jsonMapper

class CacheNøkkelHandlerTest : DescribeSpec({
    val id = "01011111111"
    val mapper = jsonMapper()
    lateinit var redisConfig: RedisCacheConfiguration
    lateinit var handler: CacheNøkkelHandler

    beforeTest {
        redisConfig = mockk()
        every { redisConfig.getKeyPrefixFor(PDL_MED_FAMILIE_CACHE.name) } returns PDL_MED_FAMILIE_CACHE.name + "::"
        every { redisConfig.getKeyPrefixFor(OID_CACHE.name) } returns OID_CACHE.name  + "::"
        handler = CacheNøkkelHandler(
            mapOf(
                PDL_MED_FAMILIE_CACHE.name to redisConfig,
                OID_CACHE.name to redisConfig
            ),
            mapper
        )
    }

    describe("CacheNøkkelHandler") {
        it("tilNøkkel legger til prefiks og nøkkel") {
            handler.nøkkel(id,OID_CACHE) shouldBe "${OID_CACHE.name}::$id"
        }
        it("tilNøkkel legger til ekstraPrefiks hvis angitt") {
            handler.nøkkel( id,PDL_MED_FAMILIE_CACHE) shouldBe "${PDL_MED_FAMILIE_CACHE.name}::${PDL_MED_FAMILIE_CACHE.extraPrefix}:$id"
        }
        it("fraNøkkel fjerner prefiks og ekstraPrefiks") {
            handler.id(handler.nøkkel(id,PDL_MED_FAMILIE_CACHE)) shouldBe id
        }
        it("fraNøkkel fjerner kun prefiks når ekstraPrefiks er null") {
            handler.id(handler.nøkkel( id,OID_CACHE)) shouldBe id
        }
        it("kaster exception hvis cache config mangler") {
            shouldThrow<IllegalStateException> {
                CacheNøkkelHandler(emptyMap(), mapper).nøkkel("key",CachableConfig("unknown"))
            }
        }
    }
})
