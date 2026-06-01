package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRUPPER_FILTER
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe

class EntraUrlTest : BehaviorSpec({

    Given("grupperURI") {
        When("isCCF er true") {
            Then("inneholder globale gruppe-IDer i OData-filter") {
                val uri = CFG.grupperURI(ANSATT_ID, true)
                val filter = uri.query.substringAfter("\$filter=").substringBefore("&")

                EntraGlobalGruppe.entries.forEach { gruppe ->
                    filter shouldContain "${gruppe.id}"
                }
                filter shouldContain "id in("
                filter shouldContain ") or $GRUPPER_FILTER"
            }
        }

        When("isCCF er false") {
            Then("inneholder kun GEO-prefix uten globale grupper") {
                val uri = CFG.grupperURI(ANSATT_ID, false)
                val filter = uri.query.substringAfter("\$filter=").substringBefore("&")

                filter shouldContain "startswith(displayName,'0000-GA-GEO')"
                EntraGlobalGruppe.entries.forEach { gruppe ->
                    filter shouldNotContain "${gruppe.id}"
                }
            }
        }

        When("ansattId settes i path") {
            Then("erstatter {ansattId} i URI-path") {
                val uri = CFG.grupperURI(ANSATT_ID, false)
                uri.path shouldContain ANSATT_ID
            }
        }
    }
}) {
    companion object {
        private const val ANSATT_ID = "Z999999"
        private val CFG = EntraGrupperConfig()
    }
}

