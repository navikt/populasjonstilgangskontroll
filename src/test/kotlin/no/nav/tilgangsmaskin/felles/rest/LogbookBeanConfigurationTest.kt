package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.OSLO
import org.zalando.logbook.HttpRequest
import java.time.Instant
import java.util.Date

class LogbookBeanConfigurationTest : BehaviorSpec({

    Given("JWT claims with exp and iat as dates") {
        When("they are normalized to a timezone") {
            Then("the timestamps are represented in OSLO timezone") {
                val claims =
                    mapOf(
                        "exp" to Date.from(Instant.parse("2026-08-19T19:09:27Z")),
                        "iat" to Date.from(Instant.parse("2026-08-19T18:04:27Z")),
                        "sub" to "12345678901",
                    )

                val normalized = claims.withTimestampsInCurrentTimezone()

                normalized["exp"] shouldBe Instant.parse("2026-08-19T19:09:27Z").atZone(OSLO)
                normalized["iat"] shouldBe Instant.parse("2026-08-19T18:04:27Z").atZone(OSLO)
                normalized["sub"] shouldBe "12345678901"
            }
        }
    }

    Given("a GraphQL introspection query body") {
        When("Logbook evaluates the body") {
            Then("the body should be ignored") {
                """{
                  "query" : "{__typename}"
                }""".shouldIgnoreGraphQlIntrospectionQuery() shouldBe true
            }
        }
    }

    Given("a GraphQL introspection request") {
        When("the request body is inspected") {
            Then("the request should be skipped entirely") {
                val request = mockk<HttpRequest> {
                    every { getBodyAsString() } returns """
                        {
                          "query" : "{__typename}"
                        }
                    """.trimIndent()
                }

                request.shouldIgnoreGraphQlIntrospectionQuery() shouldBe true
            }
        }
    }
})
