package no.nav.tilgangsmaskin.regler.motor

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

class RegelMotorBulkReglerTest(
    regelMotor: RegelMotor,
) : RegelMotorTestBase(regelMotor) {
    init {
        Given("bulkRegler") {
            When("alle brukere passerer reglene") {
                Then("alle returneres som NO_CONTENT") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker1 = BrukerBuilder(brukerId).build()
                    val bruker2 = BrukerBuilder(BrukerId("08526835644")).build()
                    val resultater = regelMotor.bulkRegler(
                        ansatt,
                        setOf(
                            BrukerOgRegelsett(bruker1, KOMPLETT_REGELTYPE),
                            BrukerOgRegelsett(bruker2, KOMPLETT_REGELTYPE)
                        )
                    )
                    assertSoftly(resultater) {
                        shouldHaveSize(2)
                        all { it.status == NO_CONTENT } shouldBe true
                    }
                }
            }

            When("en bruker avvises av regel") {
                Then("resultat inneholder både FORBIDDEN og NO_CONTENT") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val godkjentBruker = BrukerBuilder(brukerId).build()
                    val avvistBruker = BrukerBuilder(BrukerId("08526835644")).kreverMedlemskapI(STRENGT_FORTROLIG).build()
                    val resultater = regelMotor.bulkRegler(
                        ansatt,
                        setOf(
                            BrukerOgRegelsett(godkjentBruker, KOMPLETT_REGELTYPE),
                            BrukerOgRegelsett(avvistBruker, KOMPLETT_REGELTYPE)
                        )
                    )
                    assertSoftly(resultater) {
                        shouldHaveSize(2)
                        single { it.bruker == godkjentBruker }.status shouldBe NO_CONTENT
                        single { it.bruker == avvistBruker }.status shouldBe FORBIDDEN
                        single { it.bruker == avvistBruker }.regel.shouldBeInstanceOf<StrengtFortroligRegel>()
                    }
                }
            }

            When("tomt input-sett") {
                Then("returneres tomt sett") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    regelMotor.bulkRegler(ansatt, emptySet()).shouldBeEmpty()
                }
            }

            When("samme bruker evalueres med ulike regeltyper") {
                Then("regeltype påvirker utfall") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val utlandsBruker = BrukerBuilder(BrukerId("08526835644")).gt(UtenlandskTilknytning()).build()

                    val kjerne = regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(utlandsBruker, KJERNE_REGELTYPE)))
                    kjerne.single().status shouldBe NO_CONTENT

                    val komplett = regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(utlandsBruker, KOMPLETT_REGELTYPE)))
                    komplett.single().status shouldBe FORBIDDEN
                    komplett.single().regel.shouldBeInstanceOf<UtlandRegel>()
                }
            }

            When("samme bruker evalueres som komplett og overstyrbar") {
                Then("kjerne-regler ignoreres for overstyrbar type") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val skjermetForKjerne = BrukerBuilder(BrukerId("08526835644")).kreverMedlemskapI(STRENGT_FORTROLIG).build()

                    val komplett = regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(skjermetForKjerne, KOMPLETT_REGELTYPE)))
                    komplett.single().status shouldBe FORBIDDEN
                    komplett.single().regel.shouldBeInstanceOf<StrengtFortroligRegel>()

                    val overstyrbar = regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(skjermetForKjerne, OVERSTYRBAR_REGELTYPE)))
                    overstyrbar.single().status shouldBe NO_CONTENT
                }
            }

            When("en regelkall feiler med annen exception enn RegelException") {
                Then("exception videresendes") {
                    every { oppfølging.enhetFor(any()) } throws IllegalStateException("uventet feil")
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(BrukerId("08526835644")).gt(KommuneTilknytning(Kommune("9999"))).build()

                    shouldThrow<IllegalStateException> {
                        regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(bruker, OVERSTYRBAR_REGELTYPE)))
                    }
                }
            }
        }
    }
}
