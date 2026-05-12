package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.setIDs
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GEO_PREFIX
import java.util.*

class EntraUrlTest : BehaviorSpec({

    val knownIds = GlobalGruppe.entries.associate { it.property to UUID.randomUUID() }

    beforeContainer {
        setIDs(knownIds)
    }

    Given("grupperURI") {
        When("isCCF er true") {
            Then("inneholder globale gruppe-IDer i OData-filter") {
                val uri = EntraConfig().grupperURI("Z999999", true)
                val filter = uri.query.substringAfter("\$filter=").substringBefore("&")

                knownIds.values.forEach { uuid ->
                    filter shouldContain "$uuid"
                }
                filter shouldContain "id in("
                filter shouldContain ") or $GEO_PREFIX"
            }
        }

        When("isCCF er false") {
            Then("inneholder kun GEO-prefix uten globale grupper") {
                val uri = EntraConfig().grupperURI("Z999999", false)
                val filter = uri.query.substringAfter("\$filter=").substringBefore("&")

                filter shouldContain "startswith(displayName,'0000-GA-GEO')"
                knownIds.values.forEach { uuid ->
                    filter shouldNotContain "$uuid"
                }
            }
        }

        When("ansattId settes i path") {
            Then("erstatter {ansattId} i URI-path") {
                val uri = EntraConfig().grupperURI("Z999999", false)
                uri.path shouldContain "Z999999"
            }
        }
    }
})

