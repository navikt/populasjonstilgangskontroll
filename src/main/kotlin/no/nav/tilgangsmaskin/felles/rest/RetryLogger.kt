package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.utils.Auditor
import org.springframework.context.event.EventListener
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component

@Component
class RetryLogger(private val auditor: Auditor) {
    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        val failure = (event.failure as? NotFoundRestException)
            ?: (event.failure.cause as? NotFoundRestException)
            ?: event.failure
        when {
            failure is NotFoundRestException ->
                auditor.info("Aborterer metode '${event.method.name}' siden ${failure.identifikator?.verdi} ikke ble funnet på ${failure.uri}", failure)
            event.isRetryAborted ->
                auditor.warn("Aborterer metode '${event.method.name}' grunnet ${failure.javaClass.simpleName}", failure)
            else ->
                auditor.warn("Feil i metode '${event.method.name}', prøver igjen", failure)
        }
    }
}