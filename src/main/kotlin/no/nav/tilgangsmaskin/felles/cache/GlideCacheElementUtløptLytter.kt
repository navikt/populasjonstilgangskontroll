package no.nav.tilgangsmaskin.felles.cache

import glide.api.models.PubSubMessage
import glide.api.models.configuration.BaseSubscriptionConfiguration
import glide.api.models.configuration.BaseSubscriptionConfiguration.MessageCallback
import no.nav.boot.conditionals.ConditionalOnNotProd
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class GlideCacheElementUtløptLytter : MessageCallback {
    private val log = getLogger(javaClass)

    override fun accept(message: PubSubMessage, ctx: Any?) {
       log.info("Innslag utløpt i cache på kanal ${message.channel} med nøkkel ${message.message.string}")
       // publiserer.publishEvent(CacheInnslagFjernetHendelse(message.message))
    }
}