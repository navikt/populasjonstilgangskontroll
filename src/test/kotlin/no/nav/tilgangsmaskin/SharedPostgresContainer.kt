package no.nav.tilgangsmaskin

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * Singleton PostgreSQL container delt mellom alle @DataJpaTest-tester.
 * Unngår å starte en ny container per testklasse (~5-8s per oppstart).
 */
object SharedPostgresContainer {
    val postgreSQLContainer = PostgreSQLContainer("postgres:18").apply {
        start()
    }
}

