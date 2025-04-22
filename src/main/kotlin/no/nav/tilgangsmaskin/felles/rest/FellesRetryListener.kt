package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import kotlin.reflect.full.declaredFunctions

class FellesRetryListener : RetryListener {

    private val log = LoggerFactory.getLogger(javaClass)
    override fun <T : Any, E : Throwable> onSuccess(context: RetryContext, callback: RetryCallback<T, E>, result: T) {
        if (context.retryCount > 0) {
            log.info("Eksekvering av '${method(context)}' var vellykket på forsøk ${context.retryCount + 1}")
        }
    }

    override fun <T : Any, E : Throwable> onError(ctx: RetryContext, callback: RetryCallback<T, E>, e: Throwable) {
        log.warn(
                "Eksekvering av '${method(ctx)}' feilet på forsøk ${ctx.retryCount} ", ctx.lastThrowable)
    }

    companion object {
        private fun method(ctx: RetryContext): String {
            val name = ctx.getAttribute(RetryContext.NAME) as String
            return runCatching {
                val method = name.substringAfterLast('.').substringBefore('-')
                val kClass = Class.forName(name.substringBeforeLast('.')).kotlin
                return kClass.declaredFunctions.firstOrNull { it.name == method }?.name?.let { kClass.simpleName + '#' + it }
                    ?: name
            }.getOrElse {
                name
            }
        }

        const val FELLES_RETRY_LISTENER = "fellesRetryListener"
    }
}