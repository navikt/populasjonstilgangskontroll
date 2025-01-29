package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.core.annotation.AliasFor
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Retryable
annotation class RetryableOnRecoverableException(
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [],
    @get:AliasFor(annotation = Retryable::class) val maxAttempts: Int = 3,
    @get:AliasFor(annotation = Retryable::class) val backoff: Backoff = Backoff(delay = 1000)
)