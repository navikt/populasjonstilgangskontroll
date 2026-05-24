package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.graph.EntraAnsattGruppeResolver
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.NasjonalGruppeTeller

class AnsattTjenesteTest : BehaviorSpec({

    val nom      = mockk<NomTjeneste>(relaxed = true)
    val brukere  = mockk<BrukerTjeneste>(relaxed = true)
    val resolver = mockk<EntraAnsattGruppeResolver>(relaxed = true)
    val teller   = mockk<NasjonalGruppeTeller>(relaxed = true)
    val tjeneste = AnsattTjeneste(nom, brukere, resolver, teller)

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")
    val bruker   = BrukerBuilder(brukerId).build()


    Given("ansatt") {

        When("fnrForAnsatt returnerer null") {
            Then("returneres ansatt uten bruker") {
                every { nom.fnrForAnsatt(ansattId) } returns null
                tjeneste.ansatt(ansattId).bruker shouldBe null
            }
        }

        When("fnrForAnsatt returnerer en brukerId") {
            Then("returneres ansatt med bruker") {
                every { nom.fnrForAnsatt(ansattId) } returns brukerId
                every { brukere.brukerMedUtvidetFamilie(brukerId.verdi) } returns bruker
                tjeneste.ansatt(ansattId).bruker shouldBe bruker
            }
        }

        When("brukeroppslag feiler") {
            Then("returneres ansatt uten bruker") {
                every { nom.fnrForAnsatt(ansattId) } returns brukerId
                every { brukere.brukerMedUtvidetFamilie(brukerId.verdi) } throws RuntimeException("PDL nede")
                tjeneste.ansatt(ansattId).bruker shouldBe null
            }
        }

        When("resolver returnerer nasjonal-gruppe") {
            Then("er ansatt medlem av NASJONAL") {
                val ansattMedNasjonal = AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build()
                every { resolver.grupperForAnsatt(ansattId) } returns ansattMedNasjonal.grupper
                tjeneste.ansatt(ansattId) erMedlemAv NASJONAL shouldBe true
            }
        }

        When("ansatt ikke har nasjonal tilgang") {
            Then("telles false for nasjonal gruppemedlemskap") {
                every { resolver.grupperForAnsatt(ansattId) } returns emptySet()
                tjeneste.ansatt(ansattId)
                verify { teller.tell(match<Tags> { it.stream().anyMatch { tag -> tag.key == "medlem" && tag.value == "false" } }) }
            }
        }

        When("ansatt har nasjonal tilgang") {
            Then("telles true for nasjonal gruppemedlemskap") {
                every { resolver.grupperForAnsatt(ansattId) } returns AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build().grupper
                tjeneste.ansatt(ansattId)
                verify { teller.tell(match<Tags> { it.stream().anyMatch { tag -> tag.key == "medlem" && tag.value == "true" } }) }
            }
        }
    }
})
