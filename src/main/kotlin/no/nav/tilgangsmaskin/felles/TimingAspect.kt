package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class TimingAspect(private val meterRegistry: MeterRegistry) {

    @Around("execution(* no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor.intercept(..))")
    fun timeMethod(joinPoint: ProceedingJoinPoint) = Timer.builder("mslogin")
        .description("Timer med histogram for mslogin")
        .tags("method", joinPoint.signature.name)
        .publishPercentileHistogram()
        .register(meterRegistry).recordCallable { joinPoint.proceed() }
}