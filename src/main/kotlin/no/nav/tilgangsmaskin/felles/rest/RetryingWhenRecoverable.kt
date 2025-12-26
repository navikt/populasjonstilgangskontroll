package no.nav.tilgangsmaskin.felles.rest

import org.springframework.core.annotation.AliasFor
import org.springframework.resilience.annotation.Retryable
import org.springframework.web.client.ResourceAccessException
import java.lang.annotation.Inherited
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
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [RecoverableRestException::class, ResourceAccessException::class],
    /**
     * Forsinkelse i millisekunder mellom hvert retry-forsøk.
     * Standardverdi er 1000L (1 sekund).
     */
    @get:AliasFor(annotation = Retryable::class) val delay: Long = 1000L,
    /**
     * Maksimalt antall retry-forsøk før operasjonen mislykkes.
     * Standardverdi er 3 forsøk.
     */
    @get:AliasFor(annotation = Retryable::class) val maxRetries: Long = 3
    )