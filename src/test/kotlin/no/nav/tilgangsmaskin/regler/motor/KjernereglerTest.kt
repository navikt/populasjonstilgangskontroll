package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class KjernereglerTest : BehaviorSpec({

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("EgneDataRegel") {
        val regel = EgneDataRegel()

        When("ansatt er den samme som bruker") {
            Then("tilgang avvises") {
                val bruker = BrukerBuilder(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(bruker).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt er ikke den samme som bruker") {
            Then("tilgang godkjennes") {
                val bruker = BrukerBuilder(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("metadata sjekkes") {
            Then("har riktig kode og begrunnelse") {
                regel.metadata.gruppeMetadata shouldBe GruppeMetadata.EGNEDATA
                regel.kortNavn shouldBe "Egne data"
            }
        }
    }

    Given("ForeldreOgBarnRegel") {
        val regel = ForeldreOgBarnRegel()

        When("ansatt er forelder til bruker") {
            Then("tilgang avvises") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt er barn av bruker") {
            Then("tilgang avvises") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).far(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt har ingen familierelasjoner til bruker") {
            Then("tilgang godkjennes") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("metadata sjekkes") {
            Then("har riktig kode") {
                regel.metadata.gruppeMetadata shouldBe GruppeMetadata.FORELDREBARN
            }
        }
    }

    Given("PartnerRegel") {
        val regel = PartnerRegel()

        When("ansatt er partner med bruker") {
            Then("tilgang avvises") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt er ikke partner med bruker") {
            Then("tilgang godkjennes") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("metadata sjekkes") {
            Then("har riktig kode") {
                regel.metadata.gruppeMetadata shouldBe GruppeMetadata.PARTNER
            }
        }
    }

    Given("SøskenRegel") {
        val regel = SøskenRegel()

        When("ansatt er søsken til bruker") {
            Then("tilgang avvises") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).søsken(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt er ikke søsken til bruker") {
            Then("tilgang godkjennes") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("metadata sjekkes") {
            Then("har riktig kode") {
                regel.metadata.gruppeMetadata shouldBe GruppeMetadata.SØSKEN
            }
        }
    }

    Given("FellesBarnRegel") {
        val regel = FellesBarnRegel()

        When("ansatt har felles barn med bruker") {
            Then("tilgang avvises") {
                val ansattBrukerId = BrukerId("08526835644")
                val barn = BrukerId("08526835649")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(barn)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).barn(setOf(barn)).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt og bruker har ulike barn") {
            Then("tilgang godkjennes") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBarn = BrukerId("08526835649")
                val brukerBarn = BrukerId("20478606614")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(ansattBarn)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).barn(setOf(brukerBarn)).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("ingen av dem har barn") {
            Then("tilgang godkjennes") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("metadata sjekkes") {
            Then("har riktig kode") {
                regel.metadata.gruppeMetadata shouldBe GruppeMetadata.FELLES_BARN
            }
        }
    }

    Given("StrengtFortroligRegel") {
        val regel = StrengtFortroligRegel()

        When("bruker krever strengt fortrolig og ansatt mangler") {
            Then("tilgang avvises") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG).build()
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("bruker krever strengt fortrolig og ansatt har tilgang") {
            Then("tilgang godkjennes") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG).build()
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }

        When("bruker ikke krever strengt fortrolig") {
            Then("tilgang godkjennes uavhengig av ansattes grupper") {
                val bruker = BrukerBuilder(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }
    }

    Given("FortroligRegel") {
        val regel = FortroligRegel()

        When("bruker krever fortrolig og ansatt mangler") {
            Then("tilgang avvises") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.FORTROLIG).build()
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("bruker krever fortrolig og ansatt har tilgang") {
            Then("tilgang godkjennes") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.FORTROLIG).build()
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(GlobalGruppe.FORTROLIG).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }
    }

    Given("SkjermingRegel") {
        val regel = SkjermingRegel()

        When("bruker er skjermet og ansatt mangler skjermingsgruppe") {
            Then("tilgang avvises") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.SKJERMING).build()
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("bruker er skjermet og ansatt har skjermingsgruppe") {
            Then("tilgang godkjennes") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.SKJERMING).build()
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(GlobalGruppe.SKJERMING).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }
    }

    Given("StrengtFortroligUtlandRegel") {
        val regel = StrengtFortroligUtlandRegel()

        When("bruker krever strengt fortrolig utland og ansatt mangler") {
            Then("tilgang avvises") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG_UTLAND).build()
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("bruker krever strengt fortrolig utland og ansatt har strengt fortrolig") {
            Then("tilgang godkjennes fordi strengt fortrolig dekker også utland") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG_UTLAND).build()
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }
    }

    Given("KjerneRegel interface") {
        When("StrengtFortroligRegel sjekkes") {
            Then("er en KjerneRegel") {
                val regel: Regel = StrengtFortroligRegel()
                (regel is KjerneRegel) shouldBe true
            }
        }
        When("EgneDataRegel sjekkes") {
            Then("er en KjerneRegel") {
                val regel: Regel = EgneDataRegel()
                (regel is KjerneRegel) shouldBe true
            }
        }
        When("erOverstyrbar sjekkes") {
            Then("kjerneregler er ikke overstyrbare") {
                StrengtFortroligRegel().erOverstyrbar shouldBe false
                EgneDataRegel().erOverstyrbar shouldBe false
                ForeldreOgBarnRegel().erOverstyrbar shouldBe false
            }
        }
    }
})
