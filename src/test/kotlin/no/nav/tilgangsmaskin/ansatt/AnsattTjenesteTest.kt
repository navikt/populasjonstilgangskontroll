package no.nav.tilgangsmaskin.ansatt

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.NasjonalGruppeTeller
import java.util.UUID

class AnsattTjenesteTest : DescribeSpec() {

    val nom = mockk<NomTjeneste>(relaxed = true)
    @MockkBean(relaxed = true)
    val brukere = mockk<BrukerTjeneste>(relaxed = true)
    val resolver = mockk<AnsattGruppeResolver>(relaxed = true)
    val teller = mockk<NasjonalGruppeTeller>(relaxed = true)
    val tjeneste = AnsattTjeneste(nom, brukere, resolver, teller)

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")
    val bruker = BrukerBuilder(brukerId).build()

    init {
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
        describe("ansatt") {

            it("returnerer ansatt med riktig id") {
                tjeneste.ansatt(ansattId).ansattId shouldBe ansattId
            }

            it("returnerer ansatt uten bruker når fnrForAnsatt returnerer null") {
                every { nom.fnrForAnsatt(ansattId) } returns null
                tjeneste.ansatt(ansattId).bruker shouldBe null
            }

            it("returnerer ansatt med bruker når fnrForAnsatt returnerer en brukerId") {
                every { nom.fnrForAnsatt(ansattId) } returns brukerId
                every { brukere.brukerMedUtvidetFamilie(brukerId.verdi) } returns bruker
                tjeneste.ansatt(ansattId).bruker shouldBe bruker
            }

            it("returnerer ansatt uten bruker når brukeroppslag feiler") {
                every { nom.fnrForAnsatt(ansattId) } returns brukerId
                every { brukere.brukerMedUtvidetFamilie(brukerId.verdi) } throws RuntimeException("PDL nede")
                tjeneste.ansatt(ansattId).bruker shouldBe null
            }

            it("returnerer ansatt med grupper fra resolver") {
                val ansattMedNasjonal = AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build()
                every { resolver.grupperForAnsatt(ansattId) } returns ansattMedNasjonal.grupper
                tjeneste.ansatt(ansattId) erMedlemAv NASJONAL shouldBe true
            }

            it("teller nasjonal gruppemedlemskap") {
                every { resolver.grupperForAnsatt(ansattId) } returns AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build().grupper
                tjeneste.ansatt(ansattId)
                verify { teller.tell(any<Tags>()) }
            }

            it("teller false når ansatt ikke har nasjonal tilgang") {
                every { resolver.grupperForAnsatt(ansattId) } returns emptySet()
                tjeneste.ansatt(ansattId)
                verify { teller.tell(match<Tags> { it.stream().anyMatch { tag -> tag.key == "medlem" && tag.value == "false" } }) }
            }

            it("teller true når ansatt har nasjonal tilgang") {
                every { resolver.grupperForAnsatt(ansattId) } returns AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build().grupper
                tjeneste.ansatt(ansattId)
                verify { teller.tell(match<Tags> { it.stream().anyMatch { tag -> tag.key == "medlem" && tag.value == "true" } }) }
            }
        }
    }
}