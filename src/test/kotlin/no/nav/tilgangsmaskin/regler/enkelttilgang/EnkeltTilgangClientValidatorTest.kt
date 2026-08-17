package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.security.OAuth2AuthorityAddingJwtAuthenticationConverter.SystemAuthority
import no.nav.tilgangsmaskin.felles.security.StrictEnkeltTilgangAuthorizationManager
import org.springframework.security.authentication.TestingAuthenticationToken

class EnkeltTilgangClientValidatorTest : BehaviorSpec({
    val manager = StrictEnkeltTilgangAuthorizationManager(EnkeltTilgangConfig().systemer)

    fun decisionFor(systemNavn: String?) =
        manager.authorize(
            {
                val authorities = systemNavn?.let {
                    arrayOf((SystemAuthority(it)))
                } ?: emptyArray()
                TestingAuthenticationToken(
                    "principal",
                    "credentials",
                    *authorities
                )
            },
            mockk()
        ).isGranted

    Given("autorisasjon for overstyring") {
        EnkeltTilgangConfig().systemer.forEach { konsument ->
            When("konsument er godkjent ($konsument)") {
                Then("tilgang gis") {
                    decisionFor(konsument).shouldBeTrue()
                }
            }
        }

        When("konsument er ukjent") {
            Then("tilgang nektes") {
                decisionFor("ukjent-system").shouldBeFalse()
            }
        }

        When("systemnavn er utilgjengelig") {
            Then("tilgang nektes") {
                decisionFor(null).shouldBeFalse()
            }
        }
    }
})
