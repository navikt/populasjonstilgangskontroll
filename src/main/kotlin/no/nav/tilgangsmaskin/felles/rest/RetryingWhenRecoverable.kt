package no.nav.tilgangsmaskin.felles.rest

import org.springframework.core.annotation.AliasFor
import org.springframework.resilience.annotation.Retryable
import org.springframework.web.client.ResourceAccessException
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Retryable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
annotation class RetryingWhenRecoverable(
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [RecoverableRestException::class, ResourceAccessException::class],
    //@get:AliasFor(annotation = Retryable::class) val maxAttempts: Int = 3,
    //@get:AliasFor(annotation = Retryable::class) val listeners: Array<String> = [LOGGING_RETRY_LISTENER],
    @get:AliasFor(annotation = Retryable::class) val delay : Long = 1000, val maxAttempts: Int = 3)