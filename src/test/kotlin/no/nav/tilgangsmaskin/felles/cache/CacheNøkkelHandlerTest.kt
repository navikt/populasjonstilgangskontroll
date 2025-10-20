package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
class CacheNøkkelHandlerTest {

    private val key = "01011111111"
    private val UTEN_EXTRA =   CachableConfig(PDL)
    private val MED_EXTRA = UTEN_EXTRA.copy(extraPrefix = "medFamilie")
    @MockK
    private lateinit var redisConfig: RedisCacheConfiguration
    private lateinit var handler: CacheNøkkelHandler

    @BeforeEach
    fun setUp() {
        every { redisConfig.getKeyPrefixFor(MED_EXTRA.name) } returns MED_EXTRA.name
        handler = CacheNøkkelHandler(mapOf(MED_EXTRA.name to redisConfig), jacksonObjectMapper())
    }

    @Test
    @DisplayName("tilNøkkel legger til prefiks og nøkkel")
    fun toKey_leggerTilPrefiksOgNokkel() {
        assertThat(handler.tilNøkkel(UTEN_EXTRA, key)).isEqualTo("${UTEN_EXTRA.name}::$key")
    }

    @Test
    @DisplayName("tilNøkkel legger til ekstraPrefiks hvis angitt")
    fun toKey_leggerTilEkstraPrefiksHvisAngitt() {
        assertThat(handler.tilNøkkel(MED_EXTRA, key)).isEqualTo("${MED_EXTRA.name}::${MED_EXTRA.extraPrefix}:$key")
    }

    @Test
    @DisplayName("fraNøkkel fjerner prefiks og ekstraPrefiks")
    fun fromKey_fjernerPrefiksOgEkstraPrefiks() {
        assertThat(handler.idFraNøkkel(handler.tilNøkkel(MED_EXTRA, key))).isEqualTo(key)
    }

    @Test
    @DisplayName("fraNøkkel fjerner kun prefiks når ekstraPrefiks er null")
    fun fromKey_fjernerKunPrefiksNarEkstraPrefiksErNull() {
        assertThat(handler.idFraNøkkel(handler.tilNøkkel(UTEN_EXTRA, key))).isEqualTo(key)
    }

    @Test
    @DisplayName("kaster exception hvis cache config mangler")
    fun kasterExceptionHvisCacheConfigMangler() {
        assertThatThrownBy {
            CacheNøkkelHandler(emptyMap(), jacksonObjectMapper()).tilNøkkel(CachableConfig("unknown"), "key")
        }.isInstanceOf(IllegalStateException::class.java)
    }
}