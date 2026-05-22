package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations.init
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.util.UUID

class OverstyrbareReglerTest : BehaviorSpec() {

    @MockK(relaxed = true)
    private lateinit var oppfølging: OppfølgingTjeneste
    private val ansattId = AnsattId("Z999999")
    private val brukerId = BrukerId("08526835670")

    init {
        beforeSpec { init(this@OverstyrbareReglerTest) }
        beforeEach { clearAllMocks() }

        Given("UkjentBostedRegel") {
            val regel = UkjentBostedRegel()

            When("bruker har ukjent bosted og ansatt mangler ukjent-bosted-gruppe") {
                Then("tilgang avvises") {
                    val bruker = BrukerBuilder(brukerId).gt(UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()
                    val ansatt = AnsattBuilder(ansattId).build()
                    regel.evaluer(ansatt, bruker) shouldBe false
                }
            }

            When("bruker har ukjent bosted og ansatt har ukjent-bosted-gruppe") {
                Then("tilgang godkjennes") {
                    val bruker = BrukerBuilder(brukerId).gt(UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }

            When("bruker har ikke ukjent bosted") {
                Then("tilgang godkjennes") {
                    val bruker = BrukerBuilder(brukerId).build()
                    val ansatt = AnsattBuilder(ansattId).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }
        }

        Given("UtlandRegel") {
            val regel = UtlandRegel()

            When("bruker har utenlandsk bosted og ansatt mangler utlandsgruppe") {
                Then("tilgang avvises") {
                    val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    val ansatt = AnsattBuilder(ansattId).build()
                    regel.evaluer(ansatt, bruker) shouldBe false
                }
            }

            When("bruker har utenlandsk bosted og ansatt har utlandsgruppe") {
                Then("tilgang godkjennes") {
                    val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UTENLANDSK).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }

            When("bruker har ikke utenlandsk bosted") {
                Then("tilgang godkjennes") {
                    val bruker = BrukerBuilder(brukerId).build()
                    val ansatt = AnsattBuilder(ansattId).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }
        }

        Given("GeografiskRegel") {

            When("ansatt er medlem av nasjonal") {
                Then("tilgang godkjennes uansett geografisk tilknytning") {
                    val regel = GeografiskRegel(oppfølging,)
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("1234"))).build()
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(GlobalGruppe.NASJONAL).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }

            When("ansatt kan behandle brukers geografiske tilknytning via bydelsgruppe") {
                Then("tilgang godkjennes") {
                    val regel = GeografiskRegel(oppfølging)
                    val bydel = "111111"
                    val bruker = BrukerBuilder(brukerId).gt(BydelTilknytning(Bydel(bydel))).build()
                    val geoGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$bydel")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(geoGruppe).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }

            When("ansatt kan behandle brukers geografiske tilknytning via kommunegruppe") {
                Then("tilgang godkjennes") {
                    val regel = GeografiskRegel(oppfølging)
                    val kommune = "1234"
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune(kommune))).build()
                    val geoGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$kommune")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(geoGruppe).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }

            When("ansatt tilhører oppfølgingsenhet for bruker") {
                Then("tilgang godkjennes") {
                    val regel = GeografiskRegel(oppfølging)
                    val enhet = Enhetsnummer("4242")
                    every { oppfølging.enhetFor(Identifikator(brukerId.verdi)) } returns enhet
                    val enhetGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-ENHET_${enhet.verdi}")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).build()
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
            }

            When("ansatt har hverken geo-gruppe, nasjonal eller oppfølgingsenhet") {
                Then("tilgang avvises") {
                    val regel = GeografiskRegel(oppfølging)
                    every { oppfølging.enhetFor(any()) } returns null
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                    val ansatt = AnsattBuilder(ansattId).build()
                    regel.evaluer(ansatt, bruker) shouldBe false
                }
            }
        }
    }
}
