package no.nav.tilgangsmaskin.felles.cache

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.SECONDS

class CacheOperationsTest : AbstractCacheTest() {

    override fun cacheConfigurations() = mapOf(
        TEST_CACHE.name to defaultCacheConfig()
            .prefixCacheNameWith(TEST_CACHE.name)
            .disableCachingNullValues()
    )

    @BeforeEach
    fun setUp() {
        IDS.forEach { cache.delete(TEST_CACHE, it) }
    }

    @Test
    @DisplayName("Put og get en verdi, og verifiser at den er borte etter utløp")
    fun putAndGetOne() {
        putOne(T2)
        assertThat(getOne(T2.id)).isEqualTo(T2)
        await.atMost(4, SECONDS).until {
            getOne(T2.id) == null
        }
    }

    @Test
    @DisplayName("Put og get flere verdier, og verifiser at de er borte etter utløp")
    fun putAndGetMany() {
        putMany(T1, T2)
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        await.atMost(4, SECONDS).until {
            getMany(IDS).isEmpty()
        }
    }

    @Test
    @DisplayName("Delete returnerer 1 og fjerner verdien fra cachen")
    fun deleteExisting() {
        putOne(T1)
        assertThat(getOne(T1.id)).isNotNull()
        val deleted = delete(T1)
        assertThat(deleted).isEqualTo(1L)
        assertThat(getOne(T1.id)).isNull()
    }

    @Test
    @DisplayName("Delete returnerer 0 når nøkkelen ikke finnes")
    fun deleteNonExisting() {
        assertThat(getOne(T1.id)).isNull()
        val deleted = cache.delete(TEST_CACHE, T1.id)
        assertThat(deleted).isEqualTo(0L)
    }

    private fun delete( innslag: TestData) =
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
