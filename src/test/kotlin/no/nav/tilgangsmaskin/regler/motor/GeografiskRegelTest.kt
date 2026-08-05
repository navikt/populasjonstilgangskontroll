package no.nav.tilgangsmaskin.regler.motor

import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.util.UUID

class GeografiskRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker har ingen geografisk tilknytning") {
            When("ansatt er nasjonal") {
                Then("tilgang gis uten oppfølgingskall") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(EntraGlobalGruppe.NASJONAL).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    ansatt kanBehandle bruker
                    verify { oppfølging wasNot Called }
                }
            }
        }

        Given("bruker har bydelstilknytning") {
            val bydel = "111111"
            val bruker = BrukerBuilder(brukerId, BydelTilknytning(Bydel(bydel))).build()

            When("ansatt mangler bydelgruppe") {
                Then("avvises av GeografiskRegel") {
                    every { oppfølging.enhetFor(any()) } returns null
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<GeografiskRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av riktig bydelgruppe") {
                Then("tilgang gis") {
                    val bydelGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$bydel")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(bydelGruppe).build()
                    ansatt kanBehandle bruker
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
                    ansatt kanBehandle bruker
                    verify { oppfølging wasNot Called }
                }
            }

            When("ansatt mangler geografisk gruppe og ingen oppfølgingsenhet finnes") {
                Then("avvises av GeografiskRegel") {
                    every { oppfølging.enhetFor(any()) } returns null
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                    forventAvvistAv<GeografiskRegel>(ansatt, bruker)
                    verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
                }
            }

            When("ansatt har oppfølgingsgruppe for brukerens enhet") {
                Then("tilgang gis") {
                    val enhet = Enhetsnummer("4242")
                    every { oppfølging.enhetFor(Identifikator(brukerId.verdi)) } returns enhet
                    val oppfølgingGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-ENHET_${enhet.verdi}")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(oppfølgingGruppe).build()
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                    ansatt kanBehandle bruker
                    verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
                }
            }
        }
    }
}

