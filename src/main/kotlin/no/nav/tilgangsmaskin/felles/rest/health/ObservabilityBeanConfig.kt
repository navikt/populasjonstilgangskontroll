package no.nav.tilgangsmaskin.felles.rest.health

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import org.springframework.boot.actuate.endpoint.SanitizableData.SANITIZED_VALUE
import org.springframework.boot.actuate.endpoint.SanitizingFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import java.util.function.Function

private val SENSITIVE_KEYS = setOf("password", "secret", "token", "key", "credentials", "jwk", "private_key")

@Configuration
@NoCoverageAnalysis
@EnableAspectJAutoProxy
class ObservabilityBeanConfig {

    @Bean
    fun sanitizingFunction() = SanitizingFunction { data ->
        if (SENSITIVE_KEYS.any { data.key.contains(it, ignoreCase = true) }) data.withValue(SANITIZED_VALUE) else data
    }

    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, authContext: AuthContext) =
        TimedAspect(
            meterRegistry,
            Function { pjp ->
                Tags.of(
                    "cluster",
                    ClusterUtils.current.clusterName,
                    "class",
                    pjp.target.javaClass.simpleName,
                    "method",
                    pjp.signature.name,
                    "client",
                    authContext.systemNavn
                )
            }
        )

}
