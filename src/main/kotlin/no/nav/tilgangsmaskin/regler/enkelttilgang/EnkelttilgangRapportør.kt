package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.entraproxy.felles.leder.LeaderAware
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.time.measureTimedValue


@Component
@ConditionalOnGCP
class EnkelttilgangRapportør : LeaderAware() {

    private val log = getLogger(javaClass)

    @Scheduled(fixedRate = INTERVAL_MINUTES, timeUnit = MINUTES, initialDelay = 1)
    fun oppdaterCache() =
        somLeder {
            val måling = measureTimedValue {
                runCatching {

                }
            }
            måling.value.onSuccess {

            }.onFailure {
                log.warn("Periodisk cache-oppdatering feilet", it)
            }
        }

    companion object {

        private const val INTERVAL_MINUTES = 15L
    }
}