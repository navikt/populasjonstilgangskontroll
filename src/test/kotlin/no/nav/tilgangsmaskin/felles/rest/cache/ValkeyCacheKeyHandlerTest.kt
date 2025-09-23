package no.nav.tilgangsmaskin.felles.rest.cache

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.every
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.junit.jupiter.api.DisplayName
import org.assertj.core.api.Assertions.*

@ExtendWith(MockKExtension::class)
class ValkeyCacheKeyHandlerTest {

    private val key = "01011111111"
    private val UTEN_EXTRA =   CacheConfig(PDL)
    private val MED_EXTRA = UTEN_EXTRA.copy(extraPrefix = "medFamilie")
    @MockK
    private lateinit var redisConfig: RedisCacheConfiguration
    private lateinit var handler: ValkeyCacheKeyMapper

    @BeforeEach
    fun setUp() {
        every { redisConfig.getKeyPrefixFor(MED_EXTRA.name) } returns MED_EXTRA.name
        handler = ValkeyCacheKeyMapper(mapOf(MED_EXTRA.name to redisConfig))
    }

    @Test
    @DisplayName("toKey legger til prefiks og nøkkel")
    fun toKey_leggerTilPrefiksOgNokkel() {
        assertThat(handler.toKey(UTEN_EXTRA, key)).isEqualTo("${UTEN_EXTRA.name}::$key")
    }

    @Test
    @DisplayName("toKey legger til ekstraPrefiks hvis angitt")
    fun toKey_leggerTilEkstraPrefiksHvisAngitt() {
        assertThat(handler.toKey(MED_EXTRA, key)).isEqualTo("${MED_EXTRA.name}::${MED_EXTRA.extraPrefix}:$key")
    }

    @Test
    @DisplayName("fromKey fjerner prefiks og ekstraPrefiks")
    fun fromKey_fjernerPrefiksOgEkstraPrefiks() {
        assertThat(handler.fromKey(handler.toKey(MED_EXTRA, key))).isEqualTo(key)
    }

    @Test
    @DisplayName("fromKey fjerner kun prefiks når ekstraPrefiks er null")
    fun fromKey_fjernerKunPrefiksNarEkstraPrefiksErNull() {
        assertThat(handler.fromKey(handler.toKey(UTEN_EXTRA, key))).isEqualTo(key)
    }

    @Test
    @DisplayName("kaster exception hvis cache config mangler")
    fun kasterExceptionHvisCacheConfigMangler() {
        assertThatThrownBy {
            ValkeyCacheKeyMapper(emptyMap()).toKey(CacheConfig("unknown"), "key")
        }.isInstanceOf(IllegalStateException::class.java)
    }
}