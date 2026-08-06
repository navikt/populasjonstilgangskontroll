package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.security.EnkeltTilgangAuthorizationManager
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant.now

class EnkeltTilgangClientValidatorTest : BehaviorSpec({
    val cfg = EnkeltTilgangConfig()
    val manager = EnkeltTilgangAuthorizationManager(cfg.systemer)

    fun decisionFor(claims: Map<String, Any>) =
        manager.authorize(
            { TestingAuthenticationToken(jwt(claims), null) },
            mockk(relaxed = true)
        ).isGranted

    Given("autorisasjon for overstyring i prod") {
        beforeEach {
            mockkObject(ClusterUtils.Companion)
            every { ClusterUtils.isProd } returns true
        }
        afterEach {
            unmockkObject(ClusterUtils.Companion)
        }

        cfg.systemer.forEach { konsument ->
            When("konsument er godkjent ($konsument)") {
                Then("tilgang gis") {
                    decisionFor(mapOf(AZP_NAME to "dev:gcp:$konsument")).shouldBeTrue()
                }
            }
        }

        When("konsument er ukjent") {
            Then("tilgang nektes") {
                decisionFor(mapOf(AZP_NAME to "dev:gcp:ukjent-system")).shouldBeFalse()
            }
        }
    }

    Given("autorisasjon for overstyring i ikke-prod") {
        beforeEach {
            mockkObject(ClusterUtils.Companion)
            every { ClusterUtils.isProd } returns false
        }
        afterEach {
            unmockkObject(ClusterUtils.Companion)
        }

        When("hvilken som helst konsument") {
            Then("tilgang gis") {
                decisionFor(mapOf(AZP_NAME to "dev:gcp:ukjent-system")).shouldBeTrue()
                decisionFor(emptyMap()).shouldBeTrue()
            }
        }
    }
})

private fun jwt(claims: Map<String, Any>) =
    Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject("subject")
        .issuedAt(now())
        .expiresAt(now().plusSeconds(3600))
        .claims { it.putAll(claims) }
        .build()
