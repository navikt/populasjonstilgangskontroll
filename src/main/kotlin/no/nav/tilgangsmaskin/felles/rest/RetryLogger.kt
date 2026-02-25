package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import org.springframework.core.retry.RetryException
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component

@Component
class RetryLogger {
    private val log = getLogger(javaClass)

    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        val args = event.source.arguments.toSet()
        val metode = event.method.name
        when (val t = cause(event)) {
            is NotFoundRestException -> logNotFound(metode, t)
            else -> if (event.isRetryAborted) {
                logAbort(metode, args, t)
            } else {
                logRetrying(metode, args, t)
            }
        }
    }

    private fun logRetrying(metode: String, args: Set<Any?>, t: Throwable?) =
        log.warn("Feil i '$metode' ($args)  prøver igjen", t)


    private fun logNotFound(metode: String, t: NotFoundRestException) =
        log.info("NotFoundRestException fra '$metode' for [${t.identifikator}] på ${t.uri}", t)


    private fun cause(event: MethodRetryEvent)  =
        listOf(event.failure, event.failure.cause)
            .filterIsInstance<NotFoundRestException>()
            .firstOrNull()
            ?: event.failure

    private fun logAbort(method: String, args: Set<Any?>, t: Throwable) =
        if (t !is RetryException) {
            log.warn("Aborterer metode '$method}' grunnet ${t.javaClass.simpleName} $args", t) // Skjer dette noen gang?
        } else {
            log.warn("Aborterer metode '$method' grunnet ${t.cause?.javaClass?.simpleName} $args", t.cause)
        }

}