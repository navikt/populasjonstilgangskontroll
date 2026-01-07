package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOperations.CacheInnslagFjernetHendelse
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOperations.Companion.UTLØPT_KANAL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.ApplicationEventPublisher

@ConditionalOnProd
class LettuceCacheElementUtløptLytter(client: RedisClient, private val publiserer: ApplicationEventPublisher) :  RedisPubSubAdapter<String, String>() {
    private val log = getLogger(javaClass)

    init {
        client.connectPubSub().apply {
            addListener(this@LettuceCacheElementUtløptLytter)
            sync().subscribe(UTLØPT_KANAL)
        }
    }

    override fun message(kanal: String, nøkkel: String) {
        if (!kanal.startsWith(UTLØPT_KANAL)) {
            log.warn("Uventet hendelse på $kanal med nøkkel $nøkkel")
        }
        else {
            log.info("Innslag utløpt i cache på kanal $kanal med nøkkel $nøkkel")
            publiserer.publishEvent(CacheInnslagFjernetHendelse(nøkkel))
        }
    }
}

