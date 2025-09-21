package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.pubsub.RedisPubSubAdapter
import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
 class ValkeyKeyspaceRemovalListener(private val teller: BulkCacheTeller) : RedisPubSubAdapter<String, String>() {

    private val log = getLogger(javaClass)

    override fun message(channel: String, message: String) =
        if (!channel.startsWith("__keyevent@0__:expired")) {
            log.warn("Uventet keyevent på channel $channel med message $message")
        }
        else {
            val (id, cache, method) = detaljerFra(message)
            teller.tell(of("cache", cache, "result", "expired", "method", method ?: "ingen"))
            log.info("Keyspace expiry: $cache ${id.maskFnr()} $method")
        }

    private fun detaljerFra(message: String) =
        with(message.split("::", ":")) {
            Triple(last(), first(), if (size > 2) this[1] else null)
        }
}
