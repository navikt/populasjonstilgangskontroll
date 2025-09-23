package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubListener
import io.micrometer.core.instrument.Tags.of
import java.util.concurrent.atomic.AtomicInteger
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.utils.AbstractLederUtvelger
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
 class ValkeyKeyspaceRemovalListener(client: RedisClient, private val mapper: ValkeyCacheKeyMapper, private val oppfrisker: EntraCacheOppfrisker, val teller: BulkCacheTeller, erLeder: Boolean = false) : AbstractLederUtvelger(erLeder), RedisPubSubListener<String, String> {
    private val log = getLogger(javaClass)

    @Volatile
    var fjernet  = AtomicInteger(0)  // test only

     init {
         client.connectPubSub().apply {
             log.info("Starter Valkey hendelseskonsument på kanal '$CHANNEL'")
             addListener(this@ValkeyKeyspaceRemovalListener)
             sync().subscribe(CHANNEL)
         }
     }

    override fun message(channel: String, message: String) {
        if (!channel.startsWith(CHANNEL)) {
            log.warn("Uventet hendelse på channel $channel med melding $message")
        }
        else {
            if (erLeder) {
                with(mapper.detaljerFraHendelse(message)) {
                    teller.tell(of("cache", cacheName, "result", "expired", "method", metode ?: "ingen"))
                    fjernet.incrementAndGet()
                    log.info("Innslag fjernet: $cacheName ${id.maskFnr()} second")
                    if (cacheName == GRAPH) {
                        oppfrisker.oppfrisk(this, AnsattId(id))
                    }
                }
            }
        }
    }

    override fun message(pattern: String?, channel: String?, message: String?) {}
    override fun subscribed(channel: String?, count: Long) {}
    override fun psubscribed(pattern: String?, count: Long) {}
    override fun unsubscribed(channel: String?, count: Long) {}
    override fun punsubscribed(pattern: String?, count: Long) {}


    companion object {
        private const val CHANNEL = "__keyevent@0__:expired"
    }
}

