package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubListener
import io.micrometer.core.instrument.Tags.of
import java.util.concurrent.atomic.AtomicInteger
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.felles.utils.AbstractLederUtvelger
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
 class ValkeyKeyspaceRemovalListener(client: RedisClient, private val mapper: ValkeyCacheKeyMapper, private val entra: EntraTjeneste, private val oid: AnsattOidTjeneste, val teller: BulkCacheTeller, erLeder: Boolean = false) : AbstractLederUtvelger(erLeder), RedisPubSubListener<String, String> {
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
        if (!channel.startsWith("__keyevent@0__:expired")) {
            log.warn("Uventet hendelse på channel $channel med melding $message")
        }
        else {
            if (erLeder) {
                val (cache, method, id) = mapper.detaljerFra(message)
                teller.tell(of("cache", cache, "result", "expired", "method", method ?: "ingen"))
                fjernet.incrementAndGet()
                log.info("Innslag fjernet: $cache ${id.maskFnr()} $method")
                if (cache == GRAPH) {
                    if (method == "geoGrupper") {
                        log.info("Entra innslag geoGrupper fjernet: $cache ${id.maskFnr()} $method")
                        val ansattId = AnsattId(id)
                        entra.geoGrupper(ansattId, oid.oidFraEntra(ansattId))
                        log.info("Oppfrisket $cache::geoGrupper for ${ansattId.verdi} etter sletting" )
                    }
                    if (method == "geoOgGlobaleGrupper") {
                        log.info("Entra innslag geoOgGlobaleGrupper fjernet: $cache ${id.maskFnr()} $method")
                        val ansattId = AnsattId(id)
                        entra.geoOgGlobaleGrupper(ansattId, oid.oidFraEntra(ansattId))
                        log.info("Oppfrisket $cache::geoOgGlobaleGrupper  for ${ansattId.verdi} etter sletting" )
                    }
                }
            }
            else {
                log.info("Ignorerer melding, er ikke leder")
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
