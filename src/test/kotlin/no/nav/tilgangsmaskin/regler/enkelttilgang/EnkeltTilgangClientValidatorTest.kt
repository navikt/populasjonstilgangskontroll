package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.security.StrictEnkeltTilgangAuthorizationManager
import org.springframework.security.authentication.TestingAuthenticationToken

class EnkeltTilgangClientValidatorTest : BehaviorSpec({
    val cfg = EnkeltTilgangConfig()
    val token = mockk<Token>()
    val manager = StrictEnkeltTilgangAuthorizationManager(token, cfg.systemer)

    fun decisionFor() =
        manager.authorize(
            { TestingAuthenticationToken("principal", "credentials") },
            mockk()
        ).isGranted

    Given("autorisasjon for overstyring") {
        cfg.systemer.forEach { konsument ->
            When("konsument er godkjent ($konsument)") {
                Then("tilgang gis") {
                    every { token.systemNavn } returns konsument
                    decisionFor().shouldBeTrue()
                }
            }
        }

        When("konsument er ukjent") {
            Then("tilgang nektes") {
                every { token.systemNavn } returns "ukjent-system"
                decisionFor().shouldBeFalse()
            }
        }

        When("systemnavn er utilgjengelig") {
            Then("tilgang nektes") {
                every { token.systemNavn } returns "utilgjengelig"
                decisionFor().shouldBeFalse()
            }
        }
    }
})
