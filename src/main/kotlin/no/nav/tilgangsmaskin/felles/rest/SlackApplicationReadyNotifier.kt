package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration.ofDays

@Component
class SlackApplicationReadyNotifier(
    private val publisher: MessagePublisher,
    private val valkey: StringRedisTemplate,
    @param:Value("\${info.name:\${spring.application.name:populasjonstilgangskontroll}}") private val appName: String,
    @param:Value("\${info.cluster:local}") private val cluster: String,
    @param:Value("\${info.image:unknown}") private val image: String,
) {
    private val log = getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        val key = "startup-slack::$cluster::$appName::$image"

        if (erFørstePublisher(key)) {
            publisher.info("Applikasjon klar", "$appName er startet i cluster '$cluster' med image '$image'")
        }
        else log.trace("IKKE første publisher, hopper over notifikasjon")
    }

    private fun erFørstePublisher(key: String): Boolean =
        runCatching {
            valkey.opsForValue().setIfAbsent(key, "sent", ofDays(30)) == true
        }.onFailure {
            log.warn("Kunne ikke reservere startup-slack nøkkel i Valkey", it)
        }.getOrDefault(false)
}
