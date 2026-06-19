package no.nav.tilgangsmaskin.tilgang

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.http.HttpStatus.UNAUTHORIZED
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
                shouldNotThrowAny {
                    guard.krev(OBO, uri)
                }
            }
        }

        When("tokenet er CCF") {
            Then("kastes 401 Unauthorized") {
                every { token.erObo } returns false
                every { token.erCC } returns true
                shouldThrow<ResponseStatusException> {
                    guard.krev(OBO, uri)
                }.statusCode shouldBe UNAUTHORIZED
            }
        }

        When("tokenet er uautentisert") {
            Then("kastes 401 Unauthorized") {
                every { token.erObo } returns false
                every { token.erCC } returns false
                shouldThrow<ResponseStatusException> {
                    guard.krev(OBO, uri)
                }.statusCode shouldBe UNAUTHORIZED
            }
        }
    }

    Given("guarden krever CCF") {
        When("tokenet er CCF") {
            Then("kastes ingen exception") {
                every { token.erObo } returns false
                every { token.erCC } returns true
               shouldNotThrowAny {
                   guard.krev(CCF, uri)
               }
            }
        }

        When("tokenet er OBO") {
            Then("kastes 401 Unauthorized") {
                every { token.erObo } returns true
                every { token.erCC } returns false
                shouldThrow<ResponseStatusException> {
                    guard.krev(CCF, uri)
                }.statusCode shouldBe UNAUTHORIZED
            }
        }
    }
})

