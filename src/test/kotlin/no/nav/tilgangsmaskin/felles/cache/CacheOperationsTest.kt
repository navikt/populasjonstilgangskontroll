package no.nav.tilgangsmaskin.felles.cache

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.time.Duration.Companion.seconds

class CacheOperationsTest : AbstractCacheTest() {

    override fun cacheConfigurations() = mapOf(
        TEST_CACHE.name to defaultCacheConfig()
            .prefixCacheNameWith(TEST_CACHE.name)
            .disableCachingNullValues()
    )

    init {
        beforeEach {
            setUpCache()
            IDS.forEach { cache.delete(TEST_CACHE, it) }
        }

        describe("cache operasjoner") {

            it("Put og get en verdi, og verifiser at den er borte etter utløp") {
                putOne(T2)
                getOne(T2.id) shouldBe T2

                eventually(4.seconds) {
                    getOne(T2.id) shouldBe null
                }
            }

            it("Put og get flere verdier, og verifiser at de er borte etter utløp") {
                putMany(T1, T2)
                getMany(IDS).keys shouldBe IDS
                eventually(4.seconds) {
                    getMany(IDS).shouldBeEmpty()
                }
            }

            it("Delete returnerer 1 og fjerner verdien fra cachen") {
                putOne(T1)
                getOne(T1.id).shouldNotBeNull()
                delete(T1) shouldBe 1L
                getOne(T1.id).shouldBeNull()
            }

            it("Delete returnerer 0 når nøkkelen ikke finnes") {
                getOne(T1.id).shouldBeNull()
                cache.delete(TEST_CACHE, T1.id) shouldBe 0L
            }
        }
    }

    private fun delete(innslag: TestData) =
        cache.delete(TEST_CACHE, innslag.id)

    private fun putMany(vararg innslag: TestData, duration: Duration = ofSeconds(1)) =
        cache.putMany(TEST_CACHE, innslag.associateBy { it.id }, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(TEST_CACHE, ids, TestData::class)

    private fun putOne(innslag: TestData, duration: Duration = ofSeconds(2)) =
        cache.putOne(TEST_CACHE, innslag.id, innslag, duration)

    private fun getOne(id: String) =
        cache.getOne(TEST_CACHE, id, TestData::class)

    private companion object {
        val TEST_CACHE = CachableConfig("cache")
        val T1 = TestData.of(I1)
        val T2 = TestData.of(I2)
    }
}
