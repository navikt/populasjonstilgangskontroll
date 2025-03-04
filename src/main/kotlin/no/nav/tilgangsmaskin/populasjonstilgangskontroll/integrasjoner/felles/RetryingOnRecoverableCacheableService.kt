package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.FellesRetryListener.Companion.FELLES_RETRY_LISTENER
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.annotation.AliasFor
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
@Retryable
@Service
@Cacheable("set usage")
annotation class RetryingOnRecoverableCacheableService(
    @get:AliasFor(annotation = Retryable::class) val value: Array<KClass<out Throwable>> = [RecoverableRestException::class],
    @get:AliasFor(annotation = Retryable::class) val maxAttempts: Int = 3,
    @get:AliasFor(annotation = Cacheable::class) val cacheNames: Array<String> = [],
    @get:AliasFor(annotation = Retryable::class) val listeners: Array<String> = [FELLES_RETRY_LISTENER],
    @get:AliasFor(annotation = Retryable::class) val backoff: Backoff = Backoff(delay = 1000)
)