package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.util.UUID

class AnsattTest : BehaviorSpec({

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("kanBehandle") {

        When("bruker har kommunetilknytning og ansatt har matchende GEO-gruppe") {
            Then("returnerer true") {
                val kommune = "1234"
                val gruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$kommune")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(gruppe).build()
                val gt = KommuneTilknytning(Kommune(kommune))
                (ansatt kanBehandle gt) shouldBe true
            }
        }

        When("bruker har bydelstilknytning og ansatt har matchende GEO-gruppe") {
            Then("returnerer true") {
                val bydel = "123456"
                val gruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$bydel")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(gruppe).build()
                val gt = BydelTilknytning(Bydel(bydel))
                (ansatt kanBehandle gt) shouldBe true
            }
        }

        When("bruker har kommunetilknytning og ansatt mangler matchende gruppe") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                val gt = KommuneTilknytning(Kommune("9999"))
                (ansatt kanBehandle gt) shouldBe false
            }
        }

        When("bruker har udefinert tilknytning") {
            Then("returnerer true") {
                val ansatt = AnsattBuilder(ansattId).build()
                val gt = UdefinertTilknytning()
                (ansatt kanBehandle gt) shouldBe true
            }
        }

        When("bruker har ukjent bosted") {
            Then("returnerer true") {
                val ansatt = AnsattBuilder(ansattId).build()
                val gt = UkjentBosted()
                (ansatt kanBehandle gt) shouldBe true
            }
        }

        When("bruker har utenlandsk tilknytning") {
            Then("returnerer true") {
                val ansatt = AnsattBuilder(ansattId).build()
                val gt = UtenlandskTilknytning()
                (ansatt kanBehandle gt) shouldBe true
            }
        }
    }

    Given("tilhører") {

        When("ansatt har ENHET-gruppe som matcher enheten") {
            Then("returnerer true") {
                val enhet = Enhetsnummer("4242")
                val gruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-ENHET_${enhet.verdi}")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(gruppe).build()
                (ansatt tilhører enhet) shouldBe true
            }
        }

        When("ansatt mangler ENHET-gruppe for enheten") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                val enhet = Enhetsnummer("4242")
                (ansatt tilhører enhet) shouldBe false
            }
        }
    }

    Given("erMedlemAv") {

        When("ansatt har riktig global gruppe") {
            Then("returnerer true") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                (ansatt erMedlemAv FORTROLIG) shouldBe true
            }
        }

        When("ansatt har ikke riktig global gruppe") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                (ansatt erMedlemAv FORTROLIG) shouldBe false
            }
        }
    }

    Given("ikkeErMedlemAv") {

        When("ansatt mangler gruppen") {
            Then("returnerer true") {
                val ansatt = AnsattBuilder(ansattId).build()
                (ansatt ikkeErMedlemAv SKJERMING) shouldBe true
            }
        }

        When("ansatt har gruppen") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                (ansatt ikkeErMedlemAv SKJERMING) shouldBe false
            }
        }
    }

    Given("erDenSammeSom") {

        When("ansatt har bruker-identitet som matcher") {
            Then("returnerer true") {
                val bruker = BrukerBuilder(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(bruker).build()
                (ansatt erDenSammeSom bruker) shouldBe true
            }
        }

        When("ansatt har ingen bruker-identitet") {
            Then("returnerer false") {
                val bruker = BrukerBuilder(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).build()
                (ansatt erDenSammeSom bruker) shouldBe false
            }
        }

        When("ansatt har annen bruker-identitet") {
            Then("returnerer false") {
                val annenBrukerId = BrukerId("08526835644")
                val annenBruker = BrukerBuilder(annenBrukerId).build()
                val bruker = BrukerBuilder(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(annenBruker).build()
                (ansatt erDenSammeSom bruker) shouldBe false
            }
        }
    }

    Given("erNåværendeEllerTidligerePartnerMed") {

        When("ansatt er partner med bruker") {
            Then("returnerer true") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erNåværendeEllerTidligerePartnerMed bruker) shouldBe true
            }
        }

        When("ansatt er ikke partner med bruker") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erNåværendeEllerTidligerePartnerMed bruker) shouldBe false
            }
        }
    }

    Given("erForeldreEllerBarnTil") {

        When("ansatt er forelder til bruker") {
            Then("returnerer true") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erForeldreEllerBarnTil bruker) shouldBe true
            }
        }

        When("ansatt er barn av bruker") {
            Then("returnerer true") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).far(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erForeldreEllerBarnTil bruker) shouldBe true
            }
        }

        When("ansatt har ingen familierelasjoner") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erForeldreEllerBarnTil bruker) shouldBe false
            }
        }
    }

    Given("erSøskenTil") {

        When("ansatt er søsken til bruker") {
            Then("returnerer true") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).søsken(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erSøskenTil bruker) shouldBe true
            }
        }

        When("ansatt er ikke søsken til bruker") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt erSøskenTil bruker) shouldBe false
            }
        }
    }

    Given("harFellesBarnMed") {

        When("ansatt og bruker har felles barn") {
            Then("returnerer true") {
                val ansattBrukerId = BrukerId("08526835644")
                val barn = BrukerId("08526835649")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(barn)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).barn(setOf(barn)).build()
                (ansatt harFellesBarnMed bruker) shouldBe true
            }
        }

        When("ansatt og bruker har ulike barn") {
            Then("returnerer false") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBarn = BrukerId("08526835649")
                val brukerBarn = BrukerId("20478606614")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(ansattBarn)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val bruker = BrukerBuilder(brukerId).barn(setOf(brukerBarn)).build()
                (ansatt harFellesBarnMed bruker) shouldBe false
            }
        }

        When("ingen av dem har barn") {
            Then("returnerer false") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt harFellesBarnMed bruker) shouldBe false
            }
        }
    }
})
