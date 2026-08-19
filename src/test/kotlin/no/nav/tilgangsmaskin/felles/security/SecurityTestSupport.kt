package no.nav.tilgangsmaskin.felles.security

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.cache.CacheTestConfig
import no.nav.tilgangsmaskin.felles.cache.CaffeineCacheClient
import no.nav.tilgangsmaskin.felles.rest.LogbookBeanConfiguration
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.OID
import no.nav.tilgangsmaskin.felles.security.SecurityTestOAuth2.server
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.NAIS_CLUSTER_NAME
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.tilgang.BulkTilgangController
import no.nav.tilgangsmaskin.tilgang.TilgangController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import java.util.UUID

internal val TEST_ANSATT_ID = AnsattId("Z999999")
internal val TEST_BRUKER_ID = BrukerId("08526835670")
internal const val TEST_ISSUER_ID = "azuread"
internal const val TEST_SUBJECT = "subject"
internal const val TEST_AUDIENCE = "test-audience"
internal const val INVALID_AUDIENCE = "invalid-audience"
internal const val ISSUER_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.issuer-uri"
internal const val AUDIENCES_PROPERTY = "spring.security.oauth2.resourceserver.jwt.audiences"
internal object SecurityTestOAuth2 {
    val server = MockOAuth2Server().also { it.start() }
}

internal fun jwt(aud: String, ansattId: AnsattId, claims: Map<String,Any> = emptyMap(), ) = server.issueToken(
    TEST_ISSUER_ID, TEST_SUBJECT, aud,
    mapOf(NAVIDENT to ansattId.verdi, OID to "${UUID.randomUUID()}") + claims,
).serialize()

internal fun DynamicPropertyRegistry.setProperties(clusterName: String? = null) {
    add(ISSUER_URI_PROPERTY, server.issuerUrl(TEST_ISSUER_ID)::toString)
    add(AUDIENCES_PROPERTY, TEST_AUDIENCE::toString)
    clusterName?.let { add(NAIS_CLUSTER_NAME) { it } }
}

@TestConfiguration
class PdlTestConfig : CacheTestConfig(PDL)

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class, FlywayAutoConfiguration::class])
@Import(
    OAuth2SecurityBeanConfig::class,
    LogbookBeanConfiguration::class,
    TilgangController::class,
    BulkTilgangController::class,
    EnkeltTilgangController::class,
    PdlTestConfig::class,
    CaffeineCacheClient::class
)
class SecurityTestApplication
