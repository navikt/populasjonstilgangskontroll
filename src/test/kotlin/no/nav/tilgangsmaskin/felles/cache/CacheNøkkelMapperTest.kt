package no.nav.tilgangsmaskin.felles.cache

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import org.springframework.data.redis.cache.RedisCacheConfiguration

class CacheNøkkelMapperTest : BehaviorSpec({
    val id = "01011111111"
    lateinit var redisConfig: RedisCacheConfiguration
    lateinit var mapper: CacheNøkkelMapper

    beforeTest {
        redisConfig = mockk()
        every { redisConfig.getKeyPrefixFor(PDL_MED_FAMILIE_CACHE.name) } returns PDL_MED_FAMILIE_CACHE.name + "::"
        every { redisConfig.getKeyPrefixFor(OID_CACHE.name) } returns OID_CACHE.name + "::"
        mapper = CacheNøkkelMapper(
            mapOf(
                PDL_MED_FAMILIE_CACHE.name to redisConfig,
                OID_CACHE.name to redisConfig
            )
        )
    }

    Given("en cache uten ekstra-prefiks") {
        When("tilNøkkel kalles") {
            Then("legger til prefiks og id") {
                mapper.tilNøkkel(OID_CACHE, id) shouldBe "${OID_CACHE.name}::$id"
            }
        }
        When("idFraNøkkel kalles") {
            Then("fjerner kun prefiks") {
                mapper.idFraNøkkel(mapper.tilNøkkel(OID_CACHE, id)) shouldBe id
            }
        }
    }

    Given("en cache med ekstra-prefiks") {
        When("tilNøkkel kalles") {
            Then("legger til prefiks, ekstra-prefiks og id") {
                mapper.tilNøkkel(PDL_MED_FAMILIE_CACHE, id) shouldBe "${PDL_MED_FAMILIE_CACHE.name}::${PDL_MED_FAMILIE_CACHE.extraPrefix}:$id"
            }
        }
        When("idFraNøkkel kalles") {
            Then("fjerner prefiks og ekstra-prefiks") {
                mapper.idFraNøkkel(mapper.tilNøkkel(PDL_MED_FAMILIE_CACHE, id)) shouldBe id
            }
        }
    }

    Given("en cache-konfigurasjon som mangler") {
        When("tilNøkkel kalles") {
            Then("kaster IllegalStateException") {
                shouldThrow<IllegalStateException> {
                    CacheNøkkelMapper(emptyMap()).tilNøkkel(CachableConfig("unknown"), "key")
                }
            }
        }
    }
})
