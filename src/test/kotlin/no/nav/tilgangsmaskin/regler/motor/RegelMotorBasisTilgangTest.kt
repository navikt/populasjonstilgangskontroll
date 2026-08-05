package no.nav.tilgangsmaskin.regler.motor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class RegelMotorBasisTilgangTest : RegelMotorTestBase() {
    init {
        Given("bruker krever ingen spesialtilganger") {
            val bruker = BrukerBuilder(brukerId).build()

            When("ansatt er strengt fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt er fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt har ingen spesialtilganger") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bruker bryter flere kjerneregler") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, FORTROLIG).build()
            val ansatt = AnsattBuilder(ansattId).build()

            When("kjerneregler evalueres") {
                Then("stoppes på første regelbrudd") {
                    shouldThrow<RegelException> {
                        regelMotor.kjerneregler(ansatt, bruker)
                    }.regel.shouldBeInstanceOf<StrengtFortroligRegel>()
                }
            }
        }

        Given("bruker bryter flere regler i komplett regelsett") {
            val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).build()
            val ansatt = AnsattBuilder(ansattId).build()

            When("komplette regler evalueres og vergemål + utland begge ville feile") {
                Then("stoppes på første regelbrudd (vergemål)") {
                    every { vergemål.alle(ansattId) } returns setOf(brukerId)
                    shouldThrow<RegelException> {
                        regelMotor.kompletteRegler(ansatt, bruker)
                    }.regel.shouldBeInstanceOf<VergemålRegel>()
                }
            }
        }
    }
}
