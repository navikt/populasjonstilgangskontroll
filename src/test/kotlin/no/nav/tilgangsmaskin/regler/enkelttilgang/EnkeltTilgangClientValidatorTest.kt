package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.security.StrictEnkeltTilgangAuthorizationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt

class EnkeltTilgangClientValidatorTest : BehaviorSpec({
    val cfg = EnkeltTilgangConfig()
    val manager = StrictEnkeltTilgangAuthorizationManager(cfg.systemer)

    fun decisionFor(azpName: String?) =
        manager.authorize(
            {
                val principal: Any = azpName?.let {
                    Jwt.withTokenValue("token")
                        .header("alg", "none")
                        .claim(AZP_NAME, it)
                        .build()
                } ?: "principal"
                TestingAuthenticationToken(
                    principal,
                    "credentials"
                )
            },
            mockk()
        ).isGranted

    Given("autorisasjon for overstyring") {
        cfg.systemer.forEach { konsument ->
            When("konsument er godkjent ($konsument)") {
                Then("tilgang gis") {
                    decisionFor("dev-gcp:tilgangsmaskin:$konsument").shouldBeTrue()
                }
            }
        }

        When("konsument er ukjent") {
            Then("tilgang nektes") {
                decisionFor("dev-gcp:tilgangsmaskin:ukjent-system").shouldBeFalse()
            }
        }

        When("systemnavn er utilgjengelig") {
            Then("tilgang nektes") {
                decisionFor(null).shouldBeFalse()
            }
        }
    }
})
