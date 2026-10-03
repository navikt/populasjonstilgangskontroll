package no.nav.tilgangsmaskin.bruker.kodeverk

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.felles.cache.CacheOperations
import no.nav.sikkerhetstjenesten.felles.leder.LeaderAware
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit
import kotlin.time.measureTimedValue

@ConditionalOnGCP
class KodeverkCache(private val kodeverk: KodeverkTjeneste, private val cache: CacheOperations) : LeaderAware(true) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = INTERVAL_MINUTES, timeUnit = TimeUnit.MINUTES, initialDelay = 1)
    fun oppdaterCache() =
        somLeder {
            val måling = measureTimedValue {
                runCatching {
                    val koder = kodeverk.koderOgNavn()
                    cache.putMany(KodeverkConfig.KODEVERK_CACHE, koder)
                    koder
                }
            }
            måling.value.onSuccess {
                log.info(
                    "Periodisk cache-oppdatering OK, la til {} koder i cache på {}ms",
                    it.size,
                    måling.duration.inWholeMilliseconds
                )
            }.onFailure {
                log.warn("Periodisk cache-oppdatering feilet", it)
            }
        }

    companion object {
        const val KODER = "koder"
        private const val INTERVAL_MINUTES = 15L
    }
}