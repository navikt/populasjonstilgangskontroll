package no.nav.tilgangsmaskin.regler.motor

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.Called
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.util.UUID

class GeografiskRegelTest : BehaviorSpec({
    val oppfølging = mockk<OppfølgingTjeneste>()
    val regel = GeografiskRegel(oppfølging)
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    beforeEach {
        clearMocks(oppfølging)
        every { oppfølging.enhetFor(any()) } returns null
    }

    Given("bruker har ingen geografisk tilknytning") {
        When("ansatt er nasjonal") {
            Then("tilgang gis uten oppfølgingskall") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(EntraGlobalGruppe.NASJONAL).build()
                val bruker = BrukerBuilder(brukerId).build()
                assertSoftly {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                    verify { oppfølging wasNot Called }
                }
            }
        }
    }

    Given("bruker har bydelstilknytning") {
        val bydel = "111111"
        val bruker = BrukerBuilder(brukerId, BydelTilknytning(Bydel(bydel))).build()

        When("ansatt mangler bydelgruppe") {
            Then("avvises av GeografiskRegel") {
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }

        When("ansatt er medlem av riktig bydelgruppe") {
            Then("tilgang gis") {
                val bydelGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$bydel")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(bydelGruppe).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }

    Given("bruker har kommunetilknytning") {
        When("ansatt har geografisk gruppe for kommunen") {
            Then("tilgang gis uten oppfølgingskall") {
                val enhet = Enhetsnummer("4242")
                val enhetGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_${enhet.verdi}")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune(enhet.verdi))).build()
                assertSoftly {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                    verify { oppfølging wasNot Called }
                }
            }
        }

        When("ansatt mangler geografisk gruppe og ingen oppfølgingsenhet finnes") {
            Then("avvises av GeografiskRegel") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                assertSoftly {
                    regel.evaluer(ansatt, bruker).shouldBeFalse()
                    verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
                }
            }
        }

        When("ansatt har oppfølgingsgruppe for brukerens enhet") {
            Then("tilgang gis") {
                val enhet = Enhetsnummer("4242")
                every { oppfølging.enhetFor(Identifikator(brukerId.verdi)) } returns enhet
                val oppfølgingGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-ENHET_${enhet.verdi}")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(oppfølgingGruppe).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                assertSoftly {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                    verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
                }
            }
        }
    }
})
