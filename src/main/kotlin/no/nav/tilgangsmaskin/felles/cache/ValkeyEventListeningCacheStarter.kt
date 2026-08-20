package no.nav.tilgangsmaskin.felles.cache

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@ConditionalOnGCP
@NoCoverageAnalysis
@Component
class ValkeyEventListeningCacheStarter(
    private val container: RedisMessageListenerContainer,
) : LeaderAware() {

    private val log = getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun startOnReady() = attemptStart()

    @Scheduled(fixedDelayString = "\${valkey.keyspace-listener.retry-delay:30s}")
    fun retryStart() = attemptStart()

    override fun doHandleLeaderChange() {
        somLeder(
            block = {
                attemptStart()
            },
            default = {
                if (container.isRunning) {
                    log.info("Stoppet Valkey keyspace listener fordi denne instansen ikke lenger er leder")
                    container.stop()
                }
            },
        )
    }

    private fun attemptStart() {
        if (container.isRunning) return

        somLeder {
            runCatching {
                log.info("Starter Valkey keyspace listener")
                container.start()
            }.onFailure {
                log.warn("Kunne ikke starte Valkey keyspace listener, prøver igjen senere", it)
            }
        }
    }
}
