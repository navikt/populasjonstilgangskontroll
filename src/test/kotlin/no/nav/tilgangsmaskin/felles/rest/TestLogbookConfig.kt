package no.nav.tilgangsmaskin.felles.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.zalando.logbook.HeaderFilter
import org.zalando.logbook.HeaderFilter.none

@Configuration
@Profile("test")
class TestLogbookConfig {
    @Bean
    fun logbookHeaderFilter(): HeaderFilter = none()
}
