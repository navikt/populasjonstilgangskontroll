package no.nav.tilgangsmaskin.felles.cache

import glide.api.models.PubSubMessage
import glide.api.models.configuration.BaseSubscriptionConfiguration.MessageCallback
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOperations.Companion.`UTLØPT_KANAL`
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class GlideCacheElementUtløptLytter(private val publiserer: ApplicationEventPublisher) : MessageCallback {
    private val log = getLogger(javaClass)

    override fun accept(message: PubSubMessage, ctx: Any?) {
        if ( !message.channel.string.startsWith(`UTLØPT_KANAL`)) {
            log.warn("Uventet hendelse på ${message.channel} med nøkkel ${message.message.string}")
        }
        else {
            log.info("Innslag utløpt i cache på kanal ${message.channel} med nøkkel ${message.message.string}")
            publiserer.publishEvent(CacheInnslagFjernetHendelse(message.channel.string))
        }
    }
}