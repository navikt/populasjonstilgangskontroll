package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.http.HttpStatus
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component

@Component
class RetryLogger {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        if (!event.isRetryAborted) {
            return
        }
        val failure = event.failure
        if (failure is IrrecoverableRestException && failure.status == HttpStatus.NOT_FOUND) {
            log.info("Aborting method ${event.method.name} not found ", failure)
        } else {
            log.warn("Aborting method ${event.method.name}  exhausted", failure)
        }
    }
}