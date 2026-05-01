package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.globaleGrupper
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.web.client.HttpClientErrorException
import java.util.*

class AnsattGruppeResolverTest : BehaviorSpec() {

    private val entra = mockk<EntraTjeneste>()
    private val token = mockk<Token>()
    private val oidTjeneste = mockk<AnsattOidTjeneste>()
    private val resolver by lazy { AnsattGruppeResolver(entra, token, oidTjeneste) }

    init {
        beforeSpec {
            GlobalGruppe.setIDs(mapOf(
                "gruppe.nasjonal"   to nasjonalId,
                "gruppe.fortrolig"  to fortroligId,
                "gruppe.strengt"    to strengtId,
                "gruppe.utland"     to UUID.randomUUID(),
                "gruppe.udefinert"  to UUID.randomUUID(),
                "gruppe.egenansatt" to UUID.randomUUID(),
            ))
        }

        beforeEach {
            clearAllMocks()
            every { oidTjeneste.oidFraEntra(ansattId) } returns oid
        }

        Given("CC-flow") {
            beforeEach { every { token.erCC } returns true }

            When("token har kjente og ukjente gruppe-IDer") {
                Then("globaleGrupper returnerer kun kjente EntraGruppe-instanser") {
                    every { token.globaleGruppeIds } returns listOf(nasjonalId, fortroligId, strengtId, oid)

                    token.globaleGrupper() shouldContainExactlyInAnyOrder setOf(
                        EntraGruppe(nasjonalId, NASJONAL.name),
                        EntraGruppe(fortroligId, FORTROLIG.name),
                        EntraGruppe(strengtId, STRENGT_FORTROLIG.name)
                    )
                }
            }

            When("token kun har ukjente gruppe-IDer") {
                Then("globaleGrupper returnerer tomt sett") {
                    every { token.globaleGruppeIds } returns listOf(UUID.randomUUID(), UUID.randomUUID())

                    token.globaleGrupper() shouldBe emptySet()
                }
            }

            When("token ikke har noen gruppe-IDer") {
                Then("globaleGrupper returnerer tomt sett") {
                    every { token.globaleGruppeIds } returns emptyList()

                    token.globaleGrupper() shouldBe emptySet()
                }
            }

            When("grupperForAnsatt kalles") {
                Then("slår opp globale og GEO-grupper i Entra") {
                    val forventet = setOf(geoGruppe, EntraGruppe(nasjonalId))
                    every { entra.geoOgGlobaleGrupper(ansattId, oid) } returns forventet

                    resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder forventet
                    verify { entra.geoOgGlobaleGrupper(ansattId, oid) }
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
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
                Then("returnerer kun globale grupper fra token — ingen Entra-oppslag") {
                    every { token.globaleGruppeIds } returns listOf(nasjonalId)

                    resolver.grupperForAnsatt(ansattId) shouldBe setOf(EntraGruppe(nasjonalId, NASJONAL.name))
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }

            When("ansatt ikke har nasjonal tilgang") {
                Then("slår opp GEO-grupper i Entra") {
                    every { token.globaleGruppeIds } returns emptyList()
                    every { entra.geoGrupper(ansattId, oid) } returns setOf(geoGruppe)

                    resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder setOf(geoGruppe)
                    verify { entra.geoGrupper(ansattId, oid) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }

            When("ansatt har noen globale grupper men ikke nasjonal") {
                Then("kombinerer globale grupper fra token med GEO-grupper fra Entra") {
                    every { token.globaleGruppeIds } returns listOf(fortroligId)
                    every { entra.geoGrupper(ansattId, oid) } returns setOf(geoGruppe)

                    resolver.grupperForAnsatt(ansattId) shouldContainExactlyInAnyOrder
                        setOf(EntraGruppe(fortroligId, FORTROLIG.name), geoGruppe)
                }
            }
        }

        Given("uautentisert") {
            beforeEach {
                every { token.erCC }  returns false
                every { token.erObo } returns false
            }

            When("miljøet er dev/test") {
                Then("slår opp globale og GEO-grupper i Entra") {
                    every { entra.geoOgGlobaleGrupper(ansattId, oid) } returns setOf(geoGruppe)

                    resolver.grupperForAnsatt(ansattId) shouldBe setOf(geoGruppe)
                    verify { entra.geoOgGlobaleGrupper(ansattId, oid) }
                }
            }

            When("miljøet er prod") {
                Then("kaster HttpClientErrorException med status 401") {
                    mockkObject(ClusterUtils)
                    try {
                        every { ClusterUtils.isProd } returns true
                        shouldThrow<HttpClientErrorException> {
                            resolver.grupperForAnsatt(ansattId)
                        }.statusCode.value() shouldBe 401
                    } finally {
                        unmockkObject(ClusterUtils)
                    }
                }
            }
        }
    }

    companion object {
        private val ansattId    = AnsattId("Z999999")
        private val oid         = UUID.randomUUID()
        private val nasjonalId  = UUID.randomUUID()
        private val fortroligId = UUID.randomUUID()
        private val strengtId   = UUID.randomUUID()
        private val geoGruppe   = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_1234")
    }
}
