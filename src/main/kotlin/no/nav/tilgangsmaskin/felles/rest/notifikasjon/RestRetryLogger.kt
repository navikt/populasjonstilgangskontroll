package no.nav.tilgangsmaskin.felles.rest.notifikasjon

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import org.springframework.core.retry.RetryException
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component

@Component
@NoCoverageAnalysis
class RestRetryLogger {
    private val log = getLogger(javaClass)

    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        val args = event.source.arguments.toSet()
        val metode = event.method.name
        when (val t = event.failure) {
            is NotFoundRestException -> log.info("NotFoundRestException fra '$metode' for [${t.identifikator}] mot ${t.uri}",
                t)
            is RetryException -> if (t.cause !is NotFoundRestException) {
                log.warn("Aborterer metode '$metode' etter ${t.exceptions.size} forsøk grunnet ${t.cause.javaClass.simpleName} $args", t)
            }
            else -> log.info("Feil i '$metode' grunnet ${t.javaClass.simpleName}", t)
        }
    }

}