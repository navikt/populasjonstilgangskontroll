package no.nav.tilgangsmaskin.bruker

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person

class BrukerTjenesteTest : DescribeSpec({

    val pdl = mockk<PdlTjeneste>(relaxed = true)
    val skjerming = mockk<SkjermingTjeneste>(relaxed = true)
    val tjeneste = BrukerTjeneste(pdl, skjerming)

    val id1 = BrukerId("08526835670")
    val id2 = BrukerId("20478606614")
    val aktørId1 = AktørId("1111111111111")
    val aktørId2 = AktørId("2222222222222")
    val kommuneGT = KommuneTilknytning(Kommune("0301"))

    fun person(brukerId: BrukerId, aktørId: AktørId, gt: GeografiskTilknytning = kommuneGT) =
        Person(brukerId = brukerId, aktørId = aktørId, geoTilknytning = gt)

    beforeEach { clearMocks(pdl, skjerming) }

    describe("brukere") {

        it("returnerer tom mengde for tom input uten å kalle PDL") {
            val result = tjeneste.brukere(emptySet())

            result.shouldBeEmpty()
            verify(exactly = 0) { pdl.personer(any()) }
        }

        it("returnerer bruker med skjermingsstatus false når ikke skjermet") {
            every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1))
            every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to false)

            val result = tjeneste.brukere(setOf(id1.verdi))

            result shouldHaveSize 1
            result.single().brukerId shouldBe id1
            result.single().påkrevdeGrupper shouldNotContain SKJERMING
        }

        it("returnerer bruker med SKJERMING-gruppe når skjermet") {
            every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1))
            every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to true)

            val result = tjeneste.brukere(setOf(id1.verdi))

            result.single() kreverMedlemskapI SKJERMING shouldBe true
        }

        it("slår opp skjerming for alle returnerte brukere") {
            every { pdl.personer(setOf(id1.verdi, id2.verdi)) } returns setOf(
                person(id1, aktørId1),
                person(id2, aktørId2)
            )
            every { skjerming.skjerminger(any()) } returns mapOf(id1 to false, id2 to true)

            val result = tjeneste.brukere(setOf(id1.verdi, id2.verdi))

            result shouldHaveSize 2
            result.first { it.brukerId == id1 } kreverMedlemskapI SKJERMING shouldBe false
            result.first { it.brukerId == id2 } kreverMedlemskapI SKJERMING shouldBe true
        }

        it("returnerer tom mengde når PDL ikke finner noen personer") {
            every { pdl.personer(setOf(id1.verdi)) } returns emptySet()

            val result = tjeneste.brukere(setOf(id1.verdi))

            result.shouldBeEmpty()
            verify(exactly = 0) { skjerming.skjerminger(any()) }
        }

        it("returnerer delmengde når PDL ikke finner alle") {
            every { pdl.personer(setOf(id1.verdi, id2.verdi)) } returns setOf(person(id1, aktørId1))
            every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to false)

            val result = tjeneste.brukere(setOf(id1.verdi, id2.verdi))

            result shouldHaveSize 1
            result.single().brukerId shouldBe id1
        }

        it("mapper geografisk tilknytning fra person") {
            val gt = UdefinertTilknytning()
            every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1, gt))
            every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to false)

            val result = tjeneste.brukere(setOf(id1.verdi))

            result.single().geografiskTilknytning shouldBe gt
        }

        it("defaulter skjerming til false når brukerId mangler i skjermingsresultat") {
            every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1))
            every { skjerming.skjerminger(listOf(id1)) } returns emptyMap()

            val result = tjeneste.brukere(setOf(id1.verdi))

            result.single() kreverMedlemskapI SKJERMING shouldBe false
        }
    }

    describe("brukerMedNærmesteFamilie") {

        it("returnerer bruker med riktig id") {
            every { pdl.medFamilie(id1.verdi) } returns person(id1, aktørId1)
            every { skjerming.skjerming(id1) } returns false

            val result = tjeneste.brukerMedNærmesteFamilie(id1.verdi)

            result.brukerId shouldBe id1
        }

        it("setter SKJERMING-gruppe når skjermet") {
            every { pdl.medFamilie(id1.verdi) } returns person(id1, aktørId1)
            every { skjerming.skjerming(id1) } returns true

            val result = tjeneste.brukerMedNærmesteFamilie(id1.verdi)

            result kreverMedlemskapI SKJERMING shouldBe true
        }

        it("kaller PDL medFamilie, ikke medUtvidetFamile") {
            every { pdl.medFamilie(id1.verdi) } returns person(id1, aktørId1)
            every { skjerming.skjerming(id1) } returns false

            tjeneste.brukerMedNærmesteFamilie(id1.verdi)

            verify(exactly = 1) { pdl.medFamilie(id1.verdi) }
            verify(exactly = 0) { pdl.medUtvidetFamile(any()) }
        }
    }

    describe("brukerMedUtvidetFamilie") {

        it("returnerer bruker med riktig id") {
            every { pdl.medUtvidetFamile(id1.verdi) } returns person(id1, aktørId1)
            every { skjerming.skjerming(id1) } returns false

            val result = tjeneste.brukerMedUtvidetFamilie(id1.verdi)

            result.brukerId shouldBe id1
        }

        it("setter SKJERMING-gruppe når skjermet") {
            every { pdl.medUtvidetFamile(id1.verdi) } returns person(id1, aktørId1)
            every { skjerming.skjerming(id1) } returns true

            val result = tjeneste.brukerMedUtvidetFamilie(id1.verdi)

            result kreverMedlemskapI SKJERMING shouldBe true
        }

        it("kaller PDL medUtvidetFamile, ikke medFamilie") {
            every { pdl.medUtvidetFamile(id1.verdi) } returns person(id1, aktørId1)
            every { skjerming.skjerming(id1) } returns false

            tjeneste.brukerMedUtvidetFamilie(id1.verdi)

            verify(exactly = 1) { pdl.medUtvidetFamile(id1.verdi) }
            verify(exactly = 0) { pdl.medFamilie(any()) }
        }
    }
})



