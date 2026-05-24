package no.nav.tilgangsmaskin.bruker

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UKJENT_BOSTED
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
        graderinger: List<Person.Gradering> = emptyList(),
        historiskeIds: Set<BrukerId> = emptySet(),
        dødsdato: LocalDate? = null,
    ) = Person(brukerId = brukerId, aktørId = aktørId, geoTilknytning = gt,
        graderinger = graderinger, historiskeIds = historiskeIds, dødsdato = dødsdato)

    Given("tilBruker - identiteter") {
        When("person mappes") {
            Then("brukerId mappes") { tilBruker(person(), false).brukerId shouldBe brukerId }
            Then("aktørId mappes") { tilBruker(person(), false).aktørId shouldBe aktørId }
            Then("geografisk tilknytning mappes") { tilBruker(person(gt = kommuneGT), false).geografiskTilknytning shouldBe kommuneGT }
            Then("dødsdato mappes") { tilBruker(person(dødsdato = dødsdato), false).dødsdato shouldBe dødsdato }
        }
        When("person har historiske ids") {
            Then("historiskeIds mappes") {
                val historisk = BrukerId("20478606614")
                tilBruker(person(historiskeIds = setOf(historisk)), false).historiskeIds shouldBe setOf(historisk)
            }
        }
    }

    Given("tilBruker - påkrevdeGrupper — gradering") {
        When("ugradert person uten skjerming") { Then("ingen grupper kreves") { tilBruker(person(graderinger = listOf(UGRADERT)), false).påkrevdeGrupper shouldBe emptySet() } }
        When("ingen graderinger og ingen skjerming") { Then("ingen grupper kreves") { tilBruker(person(), false).påkrevdeGrupper shouldBe emptySet() } }
        When("STRENGT_FORTROLIG gradering") { Then("STRENGT_FORTROLIG kreves") { tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG) } }
        When("STRENGT_FORTROLIG_UTLAND gradering") { Then("STRENGT_FORTROLIG_UTLAND kreves") { tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG_UTLAND)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG_UTLAND) } }
        When("FORTROLIG gradering") { Then("FORTROLIG kreves") { tilBruker(person(graderinger = listOf(GRAD_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(FORTROLIG) } }
        When("STRENGT_FORTROLIG og FORTROLIG") { Then("kun STRENGT_FORTROLIG kreves") { tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG, GRAD_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG) } }
        When("STRENGT_FORTROLIG og STRENGT_FORTROLIG_UTLAND") { Then("kun STRENGT_FORTROLIG kreves") { tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG_UTLAND, GRAD_STRENGT_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG) } }
    }

    Given("tilBruker - påkrevdeGrupper — skjerming") {
        When("person er skjermet") { Then("SKJERMING kreves") { tilBruker(person(), true).påkrevdeGrupper shouldBe setOf(SKJERMING) } }
        When("skjermet person med FORTROLIG") { Then("SKJERMING og FORTROLIG kreves") { tilBruker(person(graderinger = listOf(GRAD_FORTROLIG)), true).påkrevdeGrupper shouldBe setOf(FORTROLIG, SKJERMING) } }
        When("skjermet person med STRENGT_FORTROLIG") { Then("SKJERMING og STRENGT_FORTROLIG kreves") { tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG)), true).påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG, SKJERMING) } }
    }

    Given("tilBruker - påkrevdeGrupper — geografisk tilknytning") {
        When("UdefinertTilknytning") { Then("UKJENT_BOSTED kreves") { tilBruker(person(gt = UdefinertTilknytning()), false).påkrevdeGrupper shouldBe setOf(UKJENT_BOSTED) } }
        When("KommuneTilknytning") { Then("UKJENT_BOSTED kreves ikke") { tilBruker(person(gt = kommuneGT), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED } }
        When("BydelTilknytning") { Then("UKJENT_BOSTED kreves ikke") { tilBruker(person(gt = BydelTilknytning(Bydel("030101"))), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED } }
        When("UtenlandskTilknytning") { Then("UKJENT_BOSTED kreves ikke") { tilBruker(person(gt = UtenlandskTilknytning()), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED } }
        When("UkjentBosted") { Then("UKJENT_BOSTED kreves ikke") { tilBruker(person(gt = UkjentBosted()), false).påkrevdeGrupper shouldNotContain UKJENT_BOSTED } }
        When("UdefinertTilknytning og FORTROLIG") { Then("UKJENT_BOSTED og FORTROLIG kreves") { tilBruker(person(gt = UdefinertTilknytning(), graderinger = listOf(GRAD_FORTROLIG)), false).påkrevdeGrupper shouldBe setOf(FORTROLIG, UKJENT_BOSTED) } }
        When("UdefinertTilknytning og skjermet") { Then("UKJENT_BOSTED og SKJERMING kreves") { tilBruker(person(gt = UdefinertTilknytning()), true).påkrevdeGrupper shouldBe setOf(UKJENT_BOSTED, SKJERMING) } }
    }

    Given("tilBruker - kombinasjoner") {
        When("STRENGT_FORTROLIG + UdefinertTilknytning + skjermet") {
            Then("STRENGT_FORTROLIG, UKJENT_BOSTED og SKJERMING kreves") {
                tilBruker(person(gt = UdefinertTilknytning(), graderinger = listOf(GRAD_STRENGT_FORTROLIG)), true)
                    .påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG, UKJENT_BOSTED, SKJERMING)
            }
        }
        When("ugradert, ikke skjermet, kjent GT") {
            Then("ingen grupper kreves") {
                tilBruker(person(gt = kommuneGT, graderinger = listOf(UGRADERT)), false).påkrevdeGrupper shouldBe emptySet()
            }
        }
    }
})
