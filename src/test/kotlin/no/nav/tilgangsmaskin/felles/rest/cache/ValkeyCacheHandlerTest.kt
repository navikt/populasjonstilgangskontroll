package no.nav.tilgangsmaskin.felles.rest.cache

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.junit.jupiter.api.DisplayName


@ExtendWith(MockKExtension::class)
class ValkeyCacheKeyHandlerTest {

   private val key = "myKey"


    private val MED_EXTRA = CacheConfig("testCache", "extra")
    private val UTEN_EXTRA = CacheConfig("testCache")
    private val prefix = "prefix::"
    @MockK
    private lateinit var redisConfig: RedisCacheConfiguration
    private lateinit var handler: ValkeyCacheKeyHandler

    @BeforeEach
    fun setUp() {
        every { redisConfig.getKeyPrefixFor(MED_EXTRA.name) } returns prefix
        handler = ValkeyCacheKeyHandler(mapOf(MED_EXTRA.name to redisConfig))
    }

    @Test
    @DisplayName("toKey adds prefix and key")
    fun toKey_addsPrefixAndKey() {
        val result = handler.toKey(UTEN_EXTRA, key)
        assertEquals("$prefix$key", result)
    }

    @Test
    @DisplayName("toKey adds extraPrefix when provided")
    fun toKey_addsExtraPrefix() {
        val result = handler.toKey(MED_EXTRA, key)
        assertEquals("$prefix${MED_EXTRA.extraPrefix}:$key", result)
    }

    @Test
    @DisplayName("fromKey removes prefix and extraPrefix")
    fun fromKey_removesPrefixAndExtraPrefix() {
        val fullKey = "$prefix${MED_EXTRA.extraPrefix}:$key"
        val result = handler.fromKey(MED_EXTRA, fullKey)
        assertEquals(key, result)
    }

    @Test
    @DisplayName("fromKey removes only prefix when extraPrefix is null")
    fun fromKey_removesOnlyPrefix() {
        val fullKey = "$prefix$key"
        val result = handler.fromKey(UTEN_EXTRA, fullKey)
        assertEquals(key, result)
    }

    @Test
    @DisplayName("throws exception if cache config is missing")
    fun throwsExceptionIfCacheConfigMissing() {
        val handlerMissing = ValkeyCacheKeyHandler(emptyMap())
        assertThrows<IllegalStateException> {
            handlerMissing.toKey(CacheConfig("unknown"), "key")
        }
    }
}