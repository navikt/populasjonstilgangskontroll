package no.nav.tilgangsmaskin.felles.security

import no.nav.sikkerhetstjenesten.felles.cache.CacheBeanConfig
import no.nav.sikkerhetstjenesten.felles.notifikasjon.NotificationBeanConfig
import no.nav.sikkerhetstjenesten.felles.rest.DefaultRestErrorHandler
import no.nav.tilgangsmaskin.felles.cache.CaffeineCacheOperations
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.tilgang.BulkTilgangController
import no.nav.tilgangsmaskin.tilgang.TilgangController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.context.annotation.Import

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class, FlywayAutoConfiguration::class, CacheBeanConfig::class])
@Import(
    OAuth2SecurityBeanConfig::class,
    TilgangController::class,
    BulkTilgangController::class,
    EnkeltTilgangController::class,
    PdlTestConfig::class,
    NotificationBeanConfig::class,
    CaffeineCacheOperations::class,
    DefaultRestErrorHandler::class
)
class SecurityTestApplication