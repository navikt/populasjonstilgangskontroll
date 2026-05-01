package no.nav.tilgangsmaskin.bruker

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.UGRADERT
import java.time.LocalDate
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.FORTROLIG as GRAD_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG as GRAD_STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG_UTLAND as GRAD_STRENGT_FORTROLIG_UTLAND

class PersonTilBrukerMapperTest : BehaviorSpec({

    val brukerId = BrukerId("08526835670")
    val aktørId = AktørId("1234567890123")
    val kommuneGT = KommuneTilknytning(Kommune("0301"))
    val dødsdato = LocalDate.of(2024, 1, 1)

    fun person(
        gt: GeografiskTilknytning = kommuneGT,
        graderinger: Set<Person.Gradering> = emptySet(),
        historiskeIds: Set<BrukerId> = emptySet(),
        dødsdato: LocalDate? = null,
    ) = Person(
        brukerId = brukerId,
        aktørId = aktørId,
        geoTilknytning = gt,
        graderinger = graderinger,
        historiskeIds = historiskeIds,
        dødsdato = dødsdato,
    )

    Given("tilBruker - identiteter") {
        When("person mappes") {
            Then("brukerId mappes korrekt") {
                tilBruker(person(), false).brukerId shouldBe brukerId
            }
            Then("aktørId mappes korrekt") {
                tilBruker(person(), false).aktørId shouldBe aktørId
            }
            Then("historiskeIds mappes korrekt") {
                val historisk = BrukerId("20478606614")
                tilBruker(person(historiskeIds = setOf(historisk)), false).historiskeIds shouldBe setOf(historisk)
            }
            Then("dødsdato mappes korrekt") {
                tilBruker(person(dødsdato = dødsdato), false).dødsdato shouldBe dødsdato
            }
            Then("geografisk tilknytning mappes korrekt") {
                tilBruker(person(gt = kommuneGT), false).geografiskTilknytning shouldBe kommuneGT
            }
        }
    }

    Given("tilBruker - påkrevdeGrupper — gradering") {
        When("person er ugradert uten skjerming") {
            Then("er påkrevdeGrupper tom") {
                tilBruker(person(graderinger = setOf(UGRADERT)), false).påkrevdeGrupper shouldBe emptySet()
            }
        }
        When("person har ingen graderinger og ingen skjerming") {
            Then("er påkrevdeGrupper tom") {
                tilBruker(person(), false).påkrevdeGrupper shouldBe emptySet()
            }
        }
        When("person har STRENGT_FORTROLIG gradering") {
            Then("kreves STRENGT_FORTROLIG-gruppe") {
                tilBruker(person(graderinger = setOf(GRAD_STRENGT_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG)
            }
        }
        When("person har STRENGT_FORTROLIG_UTLAND gradering") {
            Then("kreves STRENGT_FORTROLIG_UTLAND-gruppe") {
                tilBruker(person(graderinger = setOf(GRAD_STRENGT_FORTROLIG_UTLAND)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG_UTLAND)
            }
        }
        When("person har FORTROLIG gradering") {
            Then("kreves FORTROLIG-gruppe") {
                tilBruker(person(graderinger = setOf(GRAD_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(FORTROLIG)
            }
        }
        When("person har både STRENGT_FORTROLIG og FORTROLIG") {
            Then("har STRENGT_FORTROLIG prioritet") {
                tilBruker(person(graderinger = setOf(GRAD_STRENGT_FORTROLIG, GRAD_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG)
            }
        }
        When("person har både STRENGT_FORTROLIG_UTLAND og STRENGT_FORTROLIG") {
            Then("har STRENGT_FORTROLIG prioritet") {
                tilBruker(person(graderinger = setOf(GRAD_STRENGT_FORTROLIG_UTLAND, GRAD_STRENGT_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG)
            }
        }
    }

    Given("tilBruker - påkrevdeGrupper — skjerming") {
        When("person er skjermet") {
            Then("legges SKJERMING til") {
                tilBruker(person(), true).påkrevdeGrupper shouldBe setOf(SKJERMING)
            }
        }
        When("person er skjermet og har FORTROLIG gradering") {
            Then("kombineres SKJERMING med FORTROLIG") {
                tilBruker(person(graderinger = setOf(GRAD_FORTROLIG)), true).påkrevdeGrupper shouldBe setOf(FORTROLIG, SKJERMING)
            }
        }
        When("person er skjermet og har STRENGT_FORTROLIG gradering") {
            Then("kombineres SKJERMING med STRENGT_FORTROLIG") {
                tilBruker(person(graderinger = setOf(GRAD_STRENGT_FORTROLIG)), true).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG, SKJERMING)
            }
        }
    }

    Given("tilBruker - påkrevdeGrupper — geografisk tilknytning") {
        When("person har UdefinertTilknytning") {
            Then("kreves UKJENT_BOSTED-gruppe") {
                tilBruker(person(UdefinertTilknytning()), false).påkrevdeGrupper shouldBe setOf(UKJENT_BOSTED)
            }
        }
        When("person har KommuneTilknytning") {
            Then("kreves ikke UKJENT_BOSTED") {
                tilBruker(person(kommuneGT), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }
        }
        When("person har BydelTilknytning") {
            Then("kreves ikke UKJENT_BOSTED") {
                tilBruker(person(BydelTilknytning(Bydel("030101"))), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }
        }
        When("person har UtenlandskTilknytning") {
            Then("kreves ikke UKJENT_BOSTED") {
                tilBruker(person(gt = UtenlandskTilknytning()), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }
        }
        When("person har UkjentBosted") {
            Then("kreves ikke UKJENT_BOSTED") {
                tilBruker(person(UkjentBosted()), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }
        }
        When("person har UdefinertTilknytning og FORTROLIG gradering") {
            Then("kombineres UKJENT_BOSTED med FORTROLIG") {
                tilBruker(person(UdefinertTilknytning(), graderinger = setOf(GRAD_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(FORTROLIG, UKJENT_BOSTED)
            }
        }
        When("person har UdefinertTilknytning og er skjermet") {
            Then("kombineres UKJENT_BOSTED med SKJERMING") {
                tilBruker(person(UdefinertTilknytning()), true).påkrevdeGrupper shouldBe setOf(UKJENT_BOSTED, SKJERMING)
            }
        }
    }

    Given("tilBruker - kombinasjoner") {
        When("person har STRENGT_FORTROLIG, UdefinertTilknytning og er skjermet") {
            Then("kombineres STRENGT_FORTROLIG, UKJENT_BOSTED og SKJERMING") {
                tilBruker(person(gt = UdefinertTilknytning(), graderinger = setOf(GRAD_STRENGT_FORTROLIG)), true)
                    .påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG, UKJENT_BOSTED, SKJERMING)
            }
        }
        When("person er ugradert, ikke skjermet og har kjent GT") {
            Then("er påkrevdeGrupper tom") {
                tilBruker(person(kommuneGT, graderinger = setOf(UGRADERT)), false).påkrevdeGrupper shouldBe emptySet()
            }
        }
    }
})
