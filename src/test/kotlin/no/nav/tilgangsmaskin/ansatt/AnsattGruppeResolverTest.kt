package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraAnsattGruppeResolver
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.tilgang.Token
import java.util.*

class AnsattGruppeResolverTest : BehaviorSpec({

    val entra       = mockk<EntraTjeneste>()
    val token       = mockk<Token>()
    val oidTjeneste = mockk<EntraOidTjeneste>()

    val ansattId  = AnsattId("Z999999")
    val oid       = UUID.randomUUID()
    val geoGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_1234")

    val resolver = EntraAnsattGruppeResolver(entra, token, oidTjeneste, mockk())

    beforeEach {
        clearAllMocks()
        every { oidTjeneste.oid(ansattId) } returns oid
    }

    Given("CC-flow") {
        beforeEach { every { token.erCC } returns true }

        When("token inneholder kjente og ukjente gruppe-IDer") {
            Then("Token.globaleGrupper returnerer kun kjente EntraGrupper") {
                every { token.globaleGruppeIds } returns setOf(NASJONAL.id, FORTROLIG.id, STRENGT_FORTROLIG.id, oid)
                token.globaleGrupper() shouldContainExactlyInAnyOrder setOf(
                    EntraGruppe(NASJONAL.id, NASJONAL.name),
                    EntraGruppe(FORTROLIG.id, FORTROLIG.name),
                    EntraGruppe(STRENGT_FORTROLIG.id, STRENGT_FORTROLIG.name)
                )
            }
        }

        When("ingen av token-gruppeIDene er kjente") {
            Then("Token.globaleGrupper returnerer tomt sett") {
                every { token.globaleGruppeIds } returns setOf(UUID.randomUUID(), UUID.randomUUID())
                token.globaleGrupper().shouldBeEmpty()
            }
        }

        When("token ikke har noen gruppe-IDer") {
            Then("Token.globaleGrupper returnerer tomt sett") {
                every { token.globaleGruppeIds } returns emptySet()
                token.globaleGrupper().shouldBeEmpty()
            }
        }

        When("grupperForAnsatt kalles") {
            Then("slår opp globale og GEO-grupper i Entra") {
                val forventet = setOf(geoGruppe, EntraGruppe(NASJONAL.id))
                every { entra.geoOgGlobaleGrupper(ansattId, oid) } returns forventet
                assertSoftly {
                    resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder forventet
                    verify { entra.geoOgGlobaleGrupper(ansattId, oid) }
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                }
            }
        }
    }

    Given("OBO-flow") {
        beforeEach {
            every { token.erCC }  returns false
            every { token.erObo } returns true
            every { token.oid }   returns oid
        }

        When("ansatt har nasjonal tilgang") {
            Then("returneres kun globale grupper uten Entra-oppslag") {
                every { token.globaleGruppeIds } returns setOf(NASJONAL.id)
                assertSoftly {
                    resolver.grupperForAnsatt(ansattId) shouldBe setOf(EntraGruppe(NASJONAL.id, NASJONAL.name))
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }
        }

        When("ansatt ikke har nasjonal tilgang") {
            Then("slås opp GEO-grupper i Entra") {
                every { token.globaleGruppeIds } returns emptySet()
                every { entra.geoGrupper(ansattId, oid) } returns setOf(geoGruppe)
                assertSoftly {
                    resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder setOf(geoGruppe)
                    verify { entra.geoGrupper(ansattId, oid) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }
        }

        When("ansatt har fortrolig-gruppe i token") {
            Then("kombineres globale grupper fra token med GEO-grupper fra Entra") {
                every { token.globaleGruppeIds } returns setOf(FORTROLIG.id)
                every { entra.geoGrupper(ansattId, oid) } returns setOf(geoGruppe)
                resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder
                    setOf(EntraGruppe(FORTROLIG.id, FORTROLIG.name), geoGruppe)
            }
        }
    }

    Given("uautentisert") {
        beforeEach {
            every { token.erCC }  returns false
            every { token.erObo } returns false
        }

        When("miljø er dev/test") {
            Then("slås opp globale og GEO-grupper i Entra") {
                every { entra.geoOgGlobaleGrupper(ansattId, oid) } returns setOf(geoGruppe)
                assertSoftly {
                    resolver.grupperForAnsatt(ansattId) shouldBe setOf(geoGruppe)
                    verify { entra.geoOgGlobaleGrupper(ansattId, oid) }
                }
            }
        }

        When("miljø er prod") {
            Then("kastes IllegalStateException") {
                mockkObject(ClusterUtils)
                every { isProd } returns true
                try {
                    shouldThrow<IllegalStateException> {
                        resolver.grupperForAnsatt(ansattId)
                    }.message shouldBe "Autentisering påkrevet i produksjonsmiljøet"
                } finally {
                    unmockkObject(ClusterUtils)
                }
            }
        }
    }
})
