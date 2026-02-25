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
        val failure = (event.failure as? NotFoundRestException)
            ?: (event.failure.cause as? NotFoundRestException)
            ?: event.failure
        log.info("Retry event $event",event.failure)
        if (failure is NotFoundRestException)
            log.info("Ikke funnet exception cause fra '${event.method.name}' for [${failure.identifikator}] på ${failure.uri} (${event.isRetryAborted}", event.failure)
        else if (event.isRetryAborted) {
            if (failure !is RetryException) {
                log.warn("Aborterer metode '${event.method.name}' grunnet ${failure.javaClass.simpleName} (${event.source.arguments.toSet()}", failure) // Skjer dette noen gang? Det er vel bare 
            } else {
                log.warn("Aborterer metode '${event.method.name}' grunnet ${failure.cause?.javaClass?.simpleName} (${event.source.arguments.toSet()}", failure.cause)
            }
        }
        else {
            log.warn("Feil i metode '${event.method.name}',(${event.source.arguments.toSet()})  prøver igjen", failure)
        }
    }
}