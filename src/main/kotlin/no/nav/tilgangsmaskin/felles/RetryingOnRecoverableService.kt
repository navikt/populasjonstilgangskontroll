package no.nav.tilgangsmaskin.felles

import no.nav.tilgangsmaskin.felles.rest.FellesRetryListener.Companion.FELLES_RETRY_LISTENER
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.core.annotation.AliasFor
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass


@Retryable
@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
@Inherited
@Service
@MustBeDocumented
annotation class RetryingOnRecoverableService(
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [RecoverableRestException::class],
    @get:AliasFor(annotation = Retryable::class) val maxAttempts: Int = 3,
    @get:AliasFor(annotation = Retryable::class) val listeners: Array<String> = [FELLES_RETRY_LISTENER],
    @get:AliasFor(annotation = Retryable::class) val backoff: Backoff = Backoff(delay = 1000))