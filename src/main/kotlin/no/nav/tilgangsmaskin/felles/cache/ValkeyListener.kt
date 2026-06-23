package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8

@Component
class ValkeyListener(private val teller: CacheOppfriskerTeller,
                     erLeder: Boolean = true,
                     private vararg val oppfriskere: CacheOppfrisker) : LeaderAware(erLeder) {
    private val log = getLogger(javaClass)

    @RedisListener(CHANNEL)
    fun onEvent(body: ByteArray) {
        somLeder("cache innslag", {
            with(CacheNøkkel(body.toString(UTF_8))) {
                log.info("Valkey expired event {} på channel {}", this@with, CHANNEL)
                oppfriskere.firstOrNull { it.cacheName == cacheName }?.run {
                    oppfrisk(this@with)
                    teller.tell(of("cache", cacheName, "result", "expired", "method", metode ?: "ingen"))
                }
            }
        }) {}
    }

    companion object {
         const val CHANNEL = "__keyevent@0__:expired"
    }
}