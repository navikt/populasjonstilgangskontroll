package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableException
import org.springframework.core.annotation.AliasFor
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Retryable
annotation class RetryingOnRecoverable(
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [RecoverableException::class],
    @get:AliasFor(annotation = Retryable::class) val maxAttempts: Int = 3,
    @get:AliasFor(annotation = Retryable::class) val backoff: Backoff = Backoff(delay = 1000)
)