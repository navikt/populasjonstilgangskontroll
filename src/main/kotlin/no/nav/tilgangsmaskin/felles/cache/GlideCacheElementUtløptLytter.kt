package no.nav.tilgangsmaskin.felles.cache

import glide.api.models.PubSubMessage
import glide.api.models.configuration.BaseSubscriptionConfiguration
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOperations.Companion.UTLØPT_KANAL
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

//@ConditionalOnNotProd
class GlideCacheElementUtløptLytter(private val publiserer: ApplicationEventPublisher) :
    BaseSubscriptionConfiguration.MessageCallback {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun accept(message: PubSubMessage, ctx: Any?) {
        if ( !message.channel.string.startsWith(UTLØPT_KANAL)) {
            log.warn("Uventet hendelse på ${message.channel} med nøkkel ${message.message.string}")
        }
        else {
            log.info("Innslag utløpt i cache på kanal ${message.channel} med nøkkel ${message.message.string}")
            publiserer.publishEvent(AbstractCacheOperations.CacheInnslagFjernetHendelse(message.channel.string))
        }
    }
}