package no.nav.tilgangsmaskin.tilgang

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.server.ResponseStatusException

class TokenTypeGuardTest : BehaviorSpec({

    val token = mockk<Token>()
    val guard = TokenTypeGuard(token)
    val uri = "/api/v1/komplett"

    Given("guarden krever OBO") {
        When("tokenet er OBO") {
            Then("kastes ingen exception") {
                every { token.erObo } returns true
                every { token.erCC } returns false
                guard.krev(TokenType.OBO, uri)
            }
        }

        When("tokenet er CCF") {
            Then("kastes 403 Forbidden med URI i meldingen") {
                every { token.erObo } returns false
                every { token.erCC } returns true
                val ex = shouldThrow<ResponseStatusException> { guard.krev(TokenType.OBO, uri) }
                ex.statusCode shouldBe FORBIDDEN
                ex.reason!! shouldBe "Mismatch mellom token type CCF og $uri"
            }
        }

        When("tokenet er uautentisert") {
            Then("kastes 403 Forbidden") {
                every { token.erObo } returns false
                every { token.erCC } returns false
                val ex = shouldThrow<ResponseStatusException> { guard.krev(TokenType.OBO, uri) }
                ex.statusCode shouldBe FORBIDDEN
            }
        }
    }

    Given("guarden krever CCF") {
        When("tokenet er CCF") {
            Then("kastes ingen exception") {
                every { token.erObo } returns false
                every { token.erCC } returns true
                guard.krev(TokenType.CCF, uri)
            }
        }

        When("tokenet er OBO") {
            Then("kastes 403 Forbidden") {
                every { token.erObo } returns true
                every { token.erCC } returns false
                val ex = shouldThrow<ResponseStatusException> { guard.krev(TokenType.CCF, uri) }
                ex.statusCode shouldBe FORBIDDEN
                ex.reason!! shouldBe "Mismatch mellom token type OBO og $uri"
            }
        }
    }
})

