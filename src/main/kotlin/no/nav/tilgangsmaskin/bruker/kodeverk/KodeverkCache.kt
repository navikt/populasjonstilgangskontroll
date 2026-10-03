package no.nav.tilgangsmaskin.bruker.kodeverk

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.felles.cache.CacheOperations
import no.nav.sikkerhetstjenesten.felles.leder.LeaderAware
import no.nav.tilgangsmaskin.bruker.kodeverk.KodeverkConfig.Companion.KODEVERK_CACHE
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit.DAYS
import kotlin.time.measureTimedValue

@ConditionalOnGCP
class KodeverkCache(private val kodeverk: KodeverkTjeneste, private val cache: CacheOperations) : LeaderAware() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = INTERVAL_DAYS, timeUnit = DAYS, initialDelayString = "1m")
    fun oppdaterCache() =
        somLeder {
            val måling = measureTimedValue {
                runCatching {
                    with(kodeverk.koderOgNavn()) {
                        cache.putMany(KODEVERK_CACHE, this)
                        size
                    }
                }
            }
            måling.value.onSuccess {
                log.info(
                    "Periodisk cache-oppdatering OK, la til {} koder i cache på {}ms",
                    it,
                    måling.duration.inWholeMilliseconds
                )
            }.onFailure {
                log.warn("Periodisk cache-oppdatering feilet", it)
            }
        }

    companion object {
        private const val INTERVAL_DAYS = 30L
    }
}