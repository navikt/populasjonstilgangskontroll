package no.nav.tilgangsmaskin.felles.cache

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import org.springframework.data.redis.cache.RedisCacheConfiguration
import tools.jackson.module.kotlin.jsonMapper

class CacheNøkkelHandlerTest : DescribeSpec({
    val id = "01011111111"
    val UTEN_EXTRA = CachableConfig(PDL)
    val MED_EXTRA = UTEN_EXTRA.copy(extraPrefix = "medFamilie")
    val mapper = jsonMapper()
    lateinit var redisConfig: RedisCacheConfiguration
    lateinit var handler: CacheNøkkelHandler

    beforeTest {
        redisConfig = mockk()
        every { redisConfig.getKeyPrefixFor(MED_EXTRA.name) } returns MED_EXTRA.name
        every { redisConfig.getKeyPrefixFor(UTEN_EXTRA.name) } returns UTEN_EXTRA.name
        handler = CacheNøkkelHandler(
            mapOf(
                MED_EXTRA.name to redisConfig,
                UTEN_EXTRA.name to redisConfig
            ),
            mapper
        )
    }

    describe("CacheNøkkelHandler") {
        it("tilNøkkel legger til prefiks og nøkkel") {
            handler.tilNøkkel(UTEN_EXTRA, id) shouldBe "${UTEN_EXTRA.name}::$id"
        }
        it("tilNøkkel legger til ekstraPrefiks hvis angitt") {
            handler.tilNøkkel(MED_EXTRA, id) shouldBe "${MED_EXTRA.name}::${MED_EXTRA.extraPrefix}:$id"
        }
        it("fraNøkkel fjerner prefiks og ekstraPrefiks") {
            handler.idFraNøkkel(handler.tilNøkkel(MED_EXTRA, id)) shouldBe id
        }
        it("fraNøkkel fjerner kun prefiks når ekstraPrefiks er null") {
            handler.idFraNøkkel(handler.tilNøkkel(UTEN_EXTRA, id)) shouldBe id
        }
        it("kaster exception hvis cache config mangler") {
            shouldThrow<IllegalStateException> {
                CacheNøkkelHandler(emptyMap(), mapper).tilNøkkel(CachableConfig("unknown"), "key")
            }
        }
    }
})
