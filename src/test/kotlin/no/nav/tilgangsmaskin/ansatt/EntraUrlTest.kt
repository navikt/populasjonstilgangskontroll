package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRUPPER_FILTER

class EntraUrlTest : BehaviorSpec({


    Given("grupperURI") {
        When("isCCF er true") {
            Then("inneholder globale gruppe-IDer i OData-filter") {
                val uri = EntraGrupperConfig().grupperURI("Z999999", true)
                val filter = uri.query.substringAfter("\$filter=").substringBefore("&")

                GlobalGruppe.entries.forEach { gruppe ->
                    filter shouldContain "${gruppe.id}"
                }
                filter shouldContain "id in("
                filter shouldContain ") or $GRUPPER_FILTER"
            }
        }

        When("isCCF er false") {
            Then("inneholder kun GEO-prefix uten globale grupper") {
                val uri = EntraGrupperConfig().grupperURI("Z999999", false)
                val filter = uri.query.substringAfter("\$filter=").substringBefore("&")

                filter shouldContain "startswith(displayName,'0000-GA-GEO')"
                GlobalGruppe.entries.forEach { gruppe ->
                    filter shouldNotContain "${gruppe.id}"
                }
            }
        }

        When("ansattId settes i path") {
            Then("erstatter {ansattId} i URI-path") {
                val uri = EntraGrupperConfig().grupperURI("Z999999", false)
                uri.path shouldContain "Z999999"
            }
        }
    }
})

