package no.nav.tilgangsmaskin

import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * Singleton PostgreSQL container delt mellom alle @DataJpaTest-tester.
 * Unngår å starte en ny container per testklasse (~5-8s per oppstart).
 */
object SharedPostgresContainer {
    val postgreSQLContainer by lazy {
        PostgreSQLContainer("postgres:18").apply {
            start()
        }
    }
}

