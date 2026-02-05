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
        if (event.isRetryAborted) {
            when (val failure = event.failure) {
                is NotFoundRestException -> log.info("Aborterer metode ${event.method.name}  siden ${failure.identifikator} ikke ble funnet", failure)

                is IrrecoverableRestException -> log.warn("Aborterer metode ${event.method.name}", failure)
            }
        }
        else {
            log.info("Retry ${event.method.name} grunnet", event.failure)
        }

    }
}