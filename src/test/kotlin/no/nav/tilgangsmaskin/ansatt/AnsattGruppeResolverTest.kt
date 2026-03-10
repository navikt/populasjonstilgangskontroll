package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.tilgang.Token
import java.util.UUID

class AnsattGruppeResolverTest : DescribeSpec() {

    @MockK
    lateinit var entra: EntraTjeneste

    @MockK
    lateinit var token: Token

    @MockK
    lateinit var oidTjeneste: AnsattOidTjeneste

    init {
        val nasjonalId  = UUID.randomUUID()
        val fortroligId = UUID.randomUUID()

        beforeSpec {
            MockKAnnotations.init(this@AnsattGruppeResolverTest)
            GlobalGruppe.setIDs(mapOf(
                "gruppe.nasjonal"   to nasjonalId,
                "gruppe.fortrolig"  to fortroligId,
                "gruppe.strengt"    to UUID.randomUUID(),
                "gruppe.utland"     to UUID.randomUUID(),
                "gruppe.udefinert"  to UUID.randomUUID(),
                "gruppe.egenansatt" to UUID.randomUUID(),
            ))
        }

        val ansattId  = AnsattId("Z999999")
        val oid       = UUID.randomUUID()
        val geoGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_1234")

        val resolver by lazy { AnsattGruppeResolver(entra, token, oidTjeneste) }

        beforeEach {
            clearAllMocks()
            every { oidTjeneste.oidFraEntra(ansattId) } returns oid
        }

        describe("CC-flow") {

            beforeEach {
                every { token.erCC } returns true
            }

            it("slår opp globale og GEO-grupper i Entra") {
                val forventet = setOf(geoGruppe, EntraGruppe(nasjonalId))
                every { entra.geoOgGlobaleGrupper(ansattId, oid) } returns forventet

                resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder forventet
                verify(exactly = 1) { entra.geoOgGlobaleGrupper(ansattId, oid) }
                verify(exactly = 0) { entra.geoGrupper(any(), any()) }
            }
        }

        describe("OBO-flow") {

            beforeEach {
                every { token.erCC }  returns false
                every { token.erObo } returns true
                every { token.oid }   returns oid
            }

            it("returnerer kun globale grupper når ansatt har nasjonal tilgang") {
                every { token.globaleGruppeIds } returns listOf(nasjonalId)

                resolver.grupperForAnsatt(ansattId) shouldBe setOf(EntraGruppe(nasjonalId, NASJONAL.name))
                verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
            }

            it("slår opp GEO-grupper i Entra når ansatt ikke har nasjonal tilgang") {
                every { token.globaleGruppeIds } returns emptyList()
                every { entra.geoGrupper(ansattId, oid) } returns setOf(geoGruppe)

                resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder setOf(geoGruppe)
                verify { entra.geoGrupper(ansattId, oid) }
                verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
            }

            it("kombinerer globale grupper fra token med GEO-grupper fra Entra") {
                every { token.globaleGruppeIds } returns listOf(fortroligId)
                every { entra.geoGrupper(ansattId, oid) } returns setOf(geoGruppe)

                resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder
                    setOf(EntraGruppe(fortroligId, FORTROLIG.name), geoGruppe)
            }
        }

        describe("uautentisert") {

            beforeEach {
                every { token.erCC }  returns false
                every { token.erObo } returns false
            }

            it("slår opp globale og GEO-grupper i Entra i dev/test") {
                every { entra.geoOgGlobaleGrupper(ansattId, oid) } returns setOf(geoGruppe)

                resolver.grupperForAnsatt(ansattId) shouldBe setOf(geoGruppe)
                verify { entra.geoOgGlobaleGrupper(ansattId, oid) }
            }
        }
    }
}
