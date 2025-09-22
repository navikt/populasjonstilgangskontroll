package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
 class ValkeyKeyspaceRemovalListener(client: RedisClient, private val mapper: ValkeyCacheKeyMapper, val teller: BulkCacheTeller) : RedisPubSubAdapter<String, String>() {
    private val log = getLogger(javaClass)

     init {
         client.connectPubSub().apply {
             log.info("Starter Valkey hendelseskonsument på kanal '$CHANNEL'")
             addListener(this@ValkeyKeyspaceRemovalListener)
             sync().subscribe(CHANNEL)
         }
     }


    override fun message(channel: String, message: String) =
        if (!channel.startsWith("__keyevent@0__:expired")) {
            log.warn("Uventet hendelse på channel $channel med melding $message")
        }
        else {
            val (cache, method, id) = mapper.detaljerFra(message)
            teller.tell(of("cache", cache, "result", "expired", "method", method ?: "ingen"))
            log.info("Keyspace expiry: $cache ${id.maskFnr()} $method")
        }


    companion object {
        private const val CHANNEL = "__keyevent@0__:expired"
    }
}
