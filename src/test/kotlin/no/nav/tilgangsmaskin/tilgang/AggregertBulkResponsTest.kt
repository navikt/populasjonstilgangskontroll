package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT

class AggregertBulkResponsTest : BehaviorSpec({

    val ansattId = AnsattId("Z999999")

    Given("AggregertBulkRespons med blandede resultater") {
        val resultater = setOf(
            ok("08526835670"),
            ok("08526835644"),
            EnkeltBulkRespons("08526835649", FORBIDDEN, "avvist"),
            EnkeltBulkRespons("20478606614", NOT_FOUND, null)
        )
        val respons = AggregertBulkRespons(ansattId, resultater)

        When("godkjente hentes") {
            Then("returnerer kun NO_CONTENT resultater") {
                respons.godkjente shouldHaveSize 2
                respons.godkjente.all { it.httpStatus == NO_CONTENT } shouldBe true
            }
        }

        When("avviste hentes") {
            Then("returnerer kun FORBIDDEN resultater") {
                respons.avviste shouldHaveSize 1
                respons.avviste.first().brukerId shouldBe "08526835649"
            }
        }

        When("ukjente hentes") {
            Then("returnerer kun NOT_FOUND resultater") {
                respons.ukjente shouldHaveSize 1
                respons.ukjente.first().brukerId shouldBe "20478606614"
            }
        }
    }

    Given("AggregertBulkRespons uten resultater") {
        val respons = AggregertBulkRespons(ansattId)

        When("filtreringsfunksjoner kalles") {
            Then("alle returnerer tomme sett") {
                respons.godkjente.shouldBeEmpty()
                respons.avviste.shouldBeEmpty()
                respons.ukjente.shouldBeEmpty()
            }
        }
    }

    Given("AggregertBulkRespons med kun godkjente") {
        val resultater = setOf(ok("08526835670"), ok("08526835644"))
        val respons = AggregertBulkRespons(ansattId, resultater)

        When("avviste og ukjente sjekkes") {
            Then("er tomme") {
                respons.avviste.shouldBeEmpty()
                respons.ukjente.shouldBeEmpty()
                respons.godkjente shouldHaveSize 2
            }
        }
    }

    Given("EnkeltBulkRespons.ok") {
        When("opprettet via companion-funksjon") {
            Then("har riktig status og null detaljer") {
                val resultat = ok("08526835670")
                resultat.brukerId shouldBe "08526835670"
                resultat.httpStatus shouldBe NO_CONTENT
                resultat.status shouldBe 204
                resultat.detaljer shouldBe null
            }
        }
    }

    Given("EnkeltBulkRespons med detaljer") {
        When("opprettet med feildetaljer") {
            Then("bevarer detaljer") {
                val detaljer = mapOf("begrunnelse" to "ingen tilgang")
                val resultat = EnkeltBulkRespons("08526835670", FORBIDDEN, detaljer)
                resultat.status shouldBe 403
                resultat.detaljer shouldBe detaljer
            }
        }
    }
})
