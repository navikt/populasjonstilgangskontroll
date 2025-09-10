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
        assertEquals("$prefix$key", handler.toKey(UTEN_EXTRA, key))
    }

    @Test
    @DisplayName("toKey adds extraPrefix when provided")
    fun toKey_addsExtraPrefix() {
        assertEquals("$prefix${MED_EXTRA.extraPrefix}:$key", handler.toKey(MED_EXTRA, key))
    }

    @Test
    @DisplayName("fromKey removes prefix and extraPrefix")
    fun fromKey_removesPrefixAndExtraPrefix() {
        assertEquals(key, handler.fromKey(MED_EXTRA, "${prefix}${MED_EXTRA.extraPrefix}:${key}"))
    }

    @Test
    @DisplayName("fromKey removes only prefix when extraPrefix is null")
    fun fromKey_removesOnlyPrefix() {
        assertEquals(key, handler.fromKey(UTEN_EXTRA, "${prefix}${key}"))
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