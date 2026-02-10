package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.utils.Auditor
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component

@Component
class RetryLogger(private val auditor: Auditor) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        val failure = (event.failure as? NotFoundRestException)
            ?: (event.failure.cause as? NotFoundRestException)
            ?: event.failure
        when {
            failure is NotFoundRestException -> {
                auditor.info("Aborterer metode '${event.method.name}' siden ${failure.identifikator?.verdi} ikke ble funnet på ${failure.uri}", failure)
                log.info("Aborterer metode '${event.method.name}' siden ${failure.identifikator} ikke ble funnet på ${failure.uri}", failure)
            }
            event.isRetryAborted ->
                log.warn("Aborterer metode '${event.method.name}' grunnet ${failure.javaClass.simpleName}", failure)
            else -> {
                log.warn("Feil i metode '${event.method.name}',( ${event.source.arguments})  prøver igjen", failure)
                log.info(event.source.arguments.toString())

            }
        }
    }
}