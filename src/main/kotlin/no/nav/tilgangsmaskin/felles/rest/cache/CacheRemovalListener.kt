package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import java.util.concurrent.atomic.AtomicInteger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
 class ValkeyKeyspaceRemovalListener(client: RedisClient, private val eventPublisher: ApplicationEventPublisher) :  RedisPubSubAdapter<String, String>() {
    private val log = getLogger(javaClass)

    @Volatile
    var fjernet  = AtomicInteger(0)  // test only

     init {
         client.connectPubSub().apply {
             addListener(this@ValkeyKeyspaceRemovalListener)
             sync().subscribe(KANAL)
         }
     }

    override fun message(kanal: String, nøkkel: String) {
        if (!kanal.startsWith(KANAL)) {
            log.warn("Uventet hendelse på $kanal med nøkkel $nøkkel")
        }
        else {
            fjernet.incrementAndGet()
            eventPublisher.publishEvent(CacheExpiredEvent(nøkkel))
        }
    }
    companion object {
        private const val KANAL = "__keyevent@0__:expired"
    }
}

data class CacheExpiredEvent(val nøkkel: String) : ApplicationEvent(nøkkel)

