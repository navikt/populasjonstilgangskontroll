package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.AliasFor
import org.springframework.resilience.annotation.Retryable
import org.springframework.resilience.retry.MethodRetryEvent
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import java.lang.annotation.Inherited
import java.net.SocketTimeoutException
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Retryable
@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
annotation class RetryingWhenRecoverable(
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [RecoverableRestException::class, SocketTimeoutException::class, ResourceAccessException::class]
)

@Component
class RetryLogger {
    private val log = getLogger(javaClass)

    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        if (event.isRetryAborted) {
            log.warn("Aborting method ${event.method.name}, retry exhausted",event.failure)
        }
        else  {
            log.info("Retrying method '${event.method.name}' time due to exception: ${event.failure}")
        }
    }
}