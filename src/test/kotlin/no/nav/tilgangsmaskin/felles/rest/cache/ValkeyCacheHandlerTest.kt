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
import no.nav.tilgangsmaskin.felles.rest.cache.CacheName


@ExtendWith(MockKExtension::class)
class ValkeyCacheKeyHandlerTest {

    private val cacheName = CacheName("testCache")
    private val prefix = "prefix::"
    @MockK
    private lateinit var redisConfig: RedisCacheConfiguration
    private lateinit var handler: ValkeyCacheKeyHandler

    @BeforeEach
    fun setUp() {
        every { redisConfig.getKeyPrefixFor(cacheName.name) } returns prefix
        handler = ValkeyCacheKeyHandler(mapOf(cacheName.name to redisConfig))
    }

    @Test
    fun `toKey adds prefix and key`() {
        val key = "myKey"
        val result = handler.toKey(cacheName, key)
        assertEquals("$prefix$key", result)
    }

    @Test
    fun `toKey adds extraPrefix when provided`() {
        val key = "myKey"
        val extraPrefix = "extra"
        val result = handler.toKey(cacheName, key, extraPrefix)
        assertEquals("$prefix$extraPrefix:$key", result)
    }

    @Test
    fun `fromKey removes prefix and extraPrefix`() {
        val key = "myKey"
        val extraPrefix = "extra"
        val fullKey = "$prefix$extraPrefix:$key"
        val result = handler.fromKey(cacheName, fullKey, extraPrefix)
        assertEquals(key, result)
    }

    @Test
    fun `fromKey removes only prefix when extraPrefix is null`() {
        val key = "myKey"
        val fullKey = "$prefix$key"
        val result = handler.fromKey(cacheName, fullKey)
        assertEquals(key, result)
    }

    @Test
    fun `throws exception if cache config is missing`() {
        val handlerMissing = ValkeyCacheKeyHandler(emptyMap())
        assertThrows<IllegalStateException> {
            handlerMissing.toKey(CacheName("unknown"), "key")
        }
    }
}