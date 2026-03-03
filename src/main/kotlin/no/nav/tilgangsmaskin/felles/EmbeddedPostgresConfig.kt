package no.nav.tilgangsmaskin.felles

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres.start
import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnLocalOrTest
class EmbeddedPostgresConfig {

    @Bean(destroyMethod = "close")
    fun embeddedPostgres()  = start()

    @Bean
    fun dataSource(embeddedPostgres: EmbeddedPostgres) = embeddedPostgres.postgresDatabase
}
