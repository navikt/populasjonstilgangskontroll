package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.bruker.BrukerId
import java.time.LocalDate

class AvdødBrukerRegelTest : DescribeSpec({

    val teller = mockk<AvdødTeller>(relaxed = true)
    val proxy = mockk<EntraProxyTjeneste>(relaxed = true)
    val auditor = mockk<Auditor>(relaxed = true)
    val regel = AvdødBrukerRegel(teller, proxy, auditor)

    val ansattId = AnsattId("Z999999")
    val ansatt = AnsattBuilder(ansattId).build()
    val brukerId = BrukerId("08526835670")

    describe("evaluer") {

        it("returnerer alltid true for levende bruker") {
            val bruker = BrukerBuilder(brukerId).build()
            regel.evaluer(ansatt, bruker) shouldBe true
        }

        it("returnerer alltid true for avdød bruker") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(1) }.build()
            regel.evaluer(ansatt, bruker) shouldBe true
        }
    }

    describe("skalTelle") {

        it("er false når bruker ikke er avdød") {
            val bruker = BrukerBuilder(brukerId).build()
            regel.skalTelle(ansatt, bruker) shouldBe false
        }

        it("er true når bruker har dødsdato") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(1) }.build()
            regel.skalTelle(ansatt, bruker) shouldBe true
        }
    }

    describe("tell") {

        it("bruker UTILGJENGELIG som enhet for død 0-6 måneder siden") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(1) }.build()

            regel.tell(ansatt, bruker)

            verify { teller.tell(any(), UTILGJENGELIG) }
            verify(exactly = 0) { proxy.enhet(any()) }
        }

        it("bruker UTILGJENGELIG som enhet for død 7-12 måneder siden") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(9) }.build()

            regel.tell(ansatt, bruker)

            verify { teller.tell(any(), UTILGJENGELIG) }
            verify(exactly = 0) { proxy.enhet(any()) }
        }

        it("henter enhet fra proxy for død 13-24 måneder siden") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(15) }.build()
            every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "NAV Testkontor")

            regel.tell(ansatt, bruker)

            verify { teller.tell(any(), "NAV Testkontor") }
            verify { proxy.enhet(ansattId) }
        }

        it("henter enhet fra proxy for død over 24 måneder siden") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(30) }.build()
            every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "NAV Testkontor")

            regel.tell(ansatt, bruker)

            verify { teller.tell(any(), "NAV Testkontor") }
            verify { proxy.enhet(ansattId) }
        }

        it("faller tilbake til UTILGJENGELIG når proxy feiler") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(15) }.build()
            every { proxy.enhet(ansattId) } throws RuntimeException("proxy feil")

            regel.tell(ansatt, bruker)

            verify { teller.tell(any(), UTILGJENGELIG) }
        }

        it("logger via auditor når enhet er kjent") {
            val bruker = BrukerBuilder(brukerId).apply { dødsdato = LocalDate.now().minusMonths(15) }.build()
            every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "NAV Testkontor")

            regel.tell(ansatt, bruker)

            verify { auditor.info(any(), null) }
        }
    }
})

