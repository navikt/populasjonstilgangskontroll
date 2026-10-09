package no.nav.tilgangsmaskin.felles

import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import java.time.Clock
import java.time.Instant.now
import java.util.Optional

@Configuration
@NoCoverageAnalysis
class TimeBeanConfig {
    @Bean
    fun clock() = Clock.systemUTC()

    @Bean(AUDITING_TIME_PROVIDER)
    fun auditingDateTimeProvider(clock: Clock) =
        DateTimeProvider { Optional.of(now(clock)) }

    companion object {
        const val AUDITING_TIME_PROVIDER = "auditingDateTimeProvider"
    }
}
