package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.Tags
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.NasjonalGruppeTeller
import java.util.UUID

class AnsattTjenesteTest : BehaviorSpec({

    val nom = mockk<NomTjeneste>(relaxed = true)
    val brukere = mockk<BrukerTjeneste>(relaxed = true)
    val resolver = mockk<AnsattGruppeResolver>(relaxed = true)
    val teller = mockk<NasjonalGruppeTeller>(relaxed = true)
    val tjeneste = AnsattTjeneste(nom, brukere, resolver, teller)

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")
    val bruker = BrukerBuilder(brukerId).build()

    beforeSpec {
        GlobalGruppe.setIDs(mapOf(
            "gruppe.nasjonal"   to UUID.randomUUID(),
            "gruppe.fortrolig"  to UUID.randomUUID(),
            "gruppe.strengt"    to UUID.randomUUID(),
            "gruppe.utland"     to UUID.randomUUID(),
            "gruppe.udefinert"  to UUID.randomUUID(),
            "gruppe.egenansatt" to UUID.randomUUID(),
        ))
    }

    Given("en ansatt") {
        When("ansatt() kalles") {
            Then("returnerer ansatt med riktig id") {
                tjeneste.ansatt(ansattId).ansattId shouldBe ansattId
            }
        }

        When("fnrForAnsatt returnerer null") {
            every { nom.fnrForAnsatt(ansattId) } returns null
            Then("returnerer ansatt uten bruker") {
                tjeneste.ansatt(ansattId).bruker shouldBe null
            }
        }

        When("fnrForAnsatt returnerer en brukerId") {
            every { nom.fnrForAnsatt(ansattId) } returns brukerId
            every { brukere.brukerMedUtvidetFamilie(brukerId.verdi) } returns bruker
            Then("returnerer ansatt med bruker") {
                tjeneste.ansatt(ansattId).bruker shouldBe bruker
            }
        }

        When("brukeroppslag feiler") {
            every { nom.fnrForAnsatt(ansattId) } returns brukerId
            every { brukere.brukerMedUtvidetFamilie(brukerId.verdi) } throws RuntimeException("PDL nede")
            Then("returnerer ansatt uten bruker") {
                tjeneste.ansatt(ansattId).bruker shouldBe null
            }
        }

        When("resolver returnerer nasjonal gruppe") {
            val ansattMedNasjonal = AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build()
            every { resolver.grupperForAnsatt(ansattId) } returns ansattMedNasjonal.grupper
            Then("returnerer ansatt med grupper fra resolver") {
                tjeneste.ansatt(ansattId) erMedlemAv NASJONAL shouldBe true
            }
            Then("teller nasjonal gruppemedlemskap") {
                tjeneste.ansatt(ansattId)
                verify { teller.tell(any<Tags>()) }
            }
            Then("teller true for nasjonal tilgang") {
                tjeneste.ansatt(ansattId)
                verify { teller.tell(match<Tags> { tags -> tags.stream().anyMatch { it.key == "medlem" && it.value == "true" } }) }
            }
        }

        When("ansatt ikke har nasjonal tilgang") {
            every { resolver.grupperForAnsatt(ansattId) } returns emptySet()
            Then("teller false for nasjonal tilgang") {
                tjeneste.ansatt(ansattId)
                verify { teller.tell(match<Tags> { tags -> tags.stream().anyMatch { it.key == "medlem" && it.value == "false" } }) }
            }
        }
    }
})
