package no.nav.tilgangsmaskin.regler.motor

import io.mockk.every
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class VergemålRegelTest : RegelMotorTestBase() {
    init {
        Given("ansatt har vergemål for bruker") {
            When("vergemål returnerer bruker") {
                Then("avvises av VergemålRegel") {
                    every { vergemål.alle(ansattId) } returns setOf(brukerId)
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    forventAvvistAv<VergemålRegel>(ansatt, bruker)
                }
            }
        }

        Given("ansatt ikke har vergemål") {
            When("vergemål returnerer tomt sett") {
                Then("tilgang gis") {
                    every { vergemål.alle(ansattId) } returns emptySet()
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("vergemålstjenesten feiler") {
            When("oppslag kaster exception") {
                Then("tilgang gis") {
                    every { vergemål.alle(ansattId) } throws RuntimeException("tjenesten er nede")
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    ansatt kanBehandle bruker
                }
            }
        }
    }
}

