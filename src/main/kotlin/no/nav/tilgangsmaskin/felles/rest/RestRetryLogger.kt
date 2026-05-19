package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import org.springframework.core.retry.RetryException
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component

@Component
class RestRetryLogger {
    private val log = getLogger(javaClass)

    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        val args = event.source.arguments.toSet()
        val metode = event.method.name
        when (val t = cause(event)) {
            is NotFoundRestException -> log.info("NotFoundRestException fra '$metode' for [${t.identifikator}] mot ${t.uri}",
                t)
            else -> if (event.isRetryAborted) {
                if (t !is RetryException) {
                    log.warn("Aborterer metode '$metode}' grunnet ${t.javaClass.simpleName} $args")
                } else {
                    log.warn("Aborterer metode '$metode' grunnet ${t.cause.javaClass.simpleName} $args", t.cause)
                }
            } else {
                log.warn("Feil i '$metode',  prøver igjen", t)
            }
        }
    }

    private fun cause(event: MethodRetryEvent) =
        listOf(event.failure, event.failure.cause)
            .filterIsInstance<NotFoundRestException>()
            .firstOrNull()
            ?: event.failure

}