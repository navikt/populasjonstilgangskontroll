package no.nav.tilgangsmaskin.felles

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import java.time.Clock
import java.time.Clock.systemDefaultZone
import java.time.Instant
import java.util.Optional

@Configuration
@NoCoverageAnalysis
class TimeBeanConfig {
    @Bean
    fun clock(): Clock = systemDefaultZone()

    @Bean(AUDITING_TIME_PROVIDER)
    fun auditingDateTimeProvider(clock: Clock) =
        DateTimeProvider { Optional.of(Instant.now(clock)) }

    companion object {
        const val AUDITING_TIME_PROVIDER = "auditingDateTimeProvider"
    }
}
