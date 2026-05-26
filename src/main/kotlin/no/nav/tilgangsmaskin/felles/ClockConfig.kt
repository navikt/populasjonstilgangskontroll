package no.nav.tilgangsmaskin.felles

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import java.time.Clock
import java.time.Instant
import java.util.Optional

/**
 * Sentral klokke-konfigurasjon. Inject `Clock` i komponenter som trenger nåtid
 * (i stedet for `Instant.now()` / `LocalDate.now()` direkte) — så blir tid testbart
 * med `Clock.fixed(...)` eller en mutbar test-klokke.
 *
 * `DateTimeProvider`-bønnen brukes av JPA-auditing (@CreatedDate / @LastModifiedDate)
 * og er knyttet via `@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")`.
 */
@Configuration(proxyBeanMethods = false)
class ClockConfig {

    @Bean
    fun clock() = Clock.systemDefaultZone()

    @Bean("auditingDateTimeProvider")
    fun auditingDateTimeProvider(clock: Clock) =
        DateTimeProvider {
            Optional.of(Instant.now(clock))
        }
}

