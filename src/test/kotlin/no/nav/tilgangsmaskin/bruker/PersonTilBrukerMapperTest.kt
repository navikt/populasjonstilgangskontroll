package no.nav.tilgangsmaskin.bruker

import io.kotest.core.spec.style.DescribeSpec
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

class PersonTilBrukerMapperTest : DescribeSpec({

    val brukerId = BrukerId("08526835670")
    val aktørId = AktørId("1234567890123")
    val kommuneGT = KommuneTilknytning(Kommune("0301"))
    val dødsdato = LocalDate.of(2024, 1, 1)

    fun person(
        gt: GeografiskTilknytning = kommuneGT,
        graderinger: List<Person.Gradering> = emptyList(),
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

    describe("tilBruker") {

        describe("identiteter") {

            it("mapper brukerId fra person") {
                val bruker = tilBruker(person(), false)
                bruker.brukerId shouldBe brukerId
            }

            it("mapper aktørId fra person") {
                val bruker = tilBruker(person(), false)
                bruker.aktørId shouldBe aktørId
            }

            it("mapper historiskeIds fra person") {
                val historisk = BrukerId("20478606614")
                val bruker = tilBruker(person(historiskeIds = setOf(historisk)), false)
                bruker.historiskeIds shouldBe setOf(historisk)
            }

            it("mapper dødsdato fra person") {
                val bruker = tilBruker(person(dødsdato = dødsdato), false)
                bruker.dødsdato shouldBe dødsdato
            }

            it("mapper geografisk tilknytning fra person") {
                val bruker = tilBruker(person(gt = kommuneGT), false)
                bruker.geografiskTilknytning shouldBe kommuneGT
            }
        }

        describe("påkrevdeGrupper — gradering") {

            it("ingen grupper for ugradert person uten skjerming") {
                val bruker = tilBruker(person(graderinger = listOf(UGRADERT)), false)
                bruker.påkrevdeGrupper shouldBe emptySet()
            }

            it("ingen grupper for person uten graderinger og uten skjerming") {
                val bruker = tilBruker(person(), false)
                bruker.påkrevdeGrupper shouldBe emptySet()
            }

            it("STRENGT_FORTROLIG for strengt fortrolig gradering") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG)), false)
                bruker.påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG)
            }

            it("STRENGT_FORTROLIG_UTLAND for strengt fortrolig utland gradering") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG_UTLAND)), false)
                bruker.påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG_UTLAND)
            }

            it("FORTROLIG for fortrolig gradering") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_FORTROLIG)), false)
                bruker.påkrevdeGrupper shouldBe setOf(FORTROLIG)
            }

            it("STRENGT_FORTROLIG har prioritet over FORTROLIG") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG, GRAD_FORTROLIG)), false)
                bruker.påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG)
            }

            it("STRENGT_FORTROLIG har prioritet over STRENGT_FORTROLIG_UTLAND") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG_UTLAND, GRAD_STRENGT_FORTROLIG)), false)
                bruker.påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG)
            }
        }

        describe("påkrevdeGrupper — skjerming") {

            it("SKJERMING legges til for skjermet person") {
                val bruker = tilBruker(person(), true)
                bruker.påkrevdeGrupper shouldBe setOf(SKJERMING)
            }

            it("SKJERMING kombineres med gradering") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_FORTROLIG)), true)
                bruker.påkrevdeGrupper shouldBe setOf(FORTROLIG, SKJERMING)
            }

            it("SKJERMING kombineres med STRENGT_FORTROLIG") {
                val bruker = tilBruker(person(graderinger = listOf(GRAD_STRENGT_FORTROLIG)), true)
                bruker.påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG, SKJERMING)
            }
        }

        describe("påkrevdeGrupper — geografisk tilknytning") {

            it("UKJENT_BOSTED for UdefinertTilknytning") {
                val bruker = tilBruker(person(gt = UdefinertTilknytning()), false)
                bruker.påkrevdeGrupper shouldBe setOf(UKJENT_BOSTED)
            }

            it("ingen UKJENT_BOSTED for KommuneTilknytning") {
                val bruker = tilBruker(person(gt = kommuneGT), false)
                bruker.påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }

            it("ingen UKJENT_BOSTED for BydelTilknytning") {
                val bruker = tilBruker(person(gt = BydelTilknytning(Bydel("030101"))), false)
                bruker.påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }

            it("ingen UKJENT_BOSTED for UtenlandskTilknytning") {
                val bruker = tilBruker(person(gt = UtenlandskTilknytning()), false)
                bruker.påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }

            it("ingen UKJENT_BOSTED for UkjentBosted") {
                val bruker = tilBruker(person(gt = UkjentBosted()), false)
                bruker.påkrevdeGrupper shouldNotContain UKJENT_BOSTED
            }

            it("UKJENT_BOSTED kombineres med gradering") {
                val bruker = tilBruker(person(gt = UdefinertTilknytning(), graderinger = listOf(GRAD_FORTROLIG)), false)
                bruker.påkrevdeGrupper shouldBe setOf(FORTROLIG, UKJENT_BOSTED)
            }

            it("UKJENT_BOSTED kombineres med skjerming") {
                val bruker = tilBruker(person(gt = UdefinertTilknytning()), true)
                bruker.påkrevdeGrupper shouldBe setOf(UKJENT_BOSTED, SKJERMING)
            }
        }

        describe("kombinasjoner") {

            it("alle tre krav kombineres: STRENGT_FORTROLIG + UKJENT_BOSTED + SKJERMING") {
                val bruker = tilBruker(
                    person(gt = UdefinertTilknytning(), graderinger = listOf(GRAD_STRENGT_FORTROLIG)),
                    true
                )
                bruker.påkrevdeGrupper shouldBe setOf(STRENGT_FORTROLIG, UKJENT_BOSTED, SKJERMING)
            }

            it("ingen krav for helt vanlig ugradert ikke-skjermet person med kjent GT") {
                val bruker = tilBruker(person(gt = kommuneGT, graderinger = listOf(UGRADERT)), false)
                bruker.påkrevdeGrupper shouldBe emptySet()
            }
        }
    }
})


