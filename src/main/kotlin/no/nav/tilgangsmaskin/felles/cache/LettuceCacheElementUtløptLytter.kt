package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import no.nav.boot.conditionals.ConditionalOnProd
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.ApplicationEventPublisher

@ConditionalOnProd
 class LettuceCacheElementUtløptLytter(client: RedisClient, private val publiserer: ApplicationEventPublisher) :  RedisPubSubAdapter<String, String>() {
    private val log = getLogger(javaClass)

     init {
         client.connectPubSub().apply {
             addListener(this@`LettuceCacheElementUtløptLytter`)
             sync().subscribe(KANAL)
         }
     }

    override fun message(kanal: String, nøkkel: String) {
        if (!kanal.startsWith(KANAL)) {
            log.warn("Uventet hendelse på $kanal med nøkkel $nøkkel")
        }
        else {
            publiserer.publishEvent(CacheInnslagFjernetHendelse(nøkkel))
        }
    }
    companion object {
        private const val KANAL = "__keyevent@0__:expired"
    }
}



