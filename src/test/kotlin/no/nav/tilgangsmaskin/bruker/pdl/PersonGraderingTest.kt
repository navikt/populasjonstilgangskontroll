package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.UGRADERT

class PersonGraderingTest : DescribeSpec({

    describe("erStrengtFortroligUtland") {

        it("returnerer true når listen inneholder STRENGT_FORTROLIG_UTLAND") {
            listOf(STRENGT_FORTROLIG_UTLAND).erStrengtFortroligUtland() shouldBe true
        }

        it("returnerer false når listen ikke inneholder STRENGT_FORTROLIG_UTLAND") {
            listOf(STRENGT_FORTROLIG, FORTROLIG, UGRADERT).erStrengtFortroligUtland() shouldBe false
        }

        it("returnerer false for tom liste") {
            emptyList<Person.Gradering>().erStrengtFortroligUtland() shouldBe false
        }
    }

    describe("erStrengtFortrolig") {

        it("returnerer true når listen inneholder STRENGT_FORTROLIG") {
            listOf(STRENGT_FORTROLIG).erStrengtFortrolig() shouldBe true
        }

        it("returnerer false når listen ikke inneholder STRENGT_FORTROLIG") {
            listOf(STRENGT_FORTROLIG_UTLAND, FORTROLIG, UGRADERT).erStrengtFortrolig() shouldBe false
        }

        it("returnerer false for tom liste") {
            emptyList<Person.Gradering>().erStrengtFortrolig() shouldBe false
        }
    }

    describe("erFortrolig") {

        it("returnerer true når listen inneholder FORTROLIG") {
            listOf(FORTROLIG).erFortrolig() shouldBe true
        }

        it("returnerer false når listen ikke inneholder FORTROLIG") {
            listOf(STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, UGRADERT).erFortrolig() shouldBe false
        }

        it("returnerer false for tom liste") {
            emptyList<Person.Gradering>().erFortrolig() shouldBe false
        }
    }
})

