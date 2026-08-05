package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE

class RegelMotorFeilpropageringTest : BehaviorSpec({
    val ansatt = AnsattBuilder(AnsattId("Z999999")).build()
    val bruker = BrukerBuilder(BrukerId("08526835670")).build()
    val logger = mockk<RegelMotorLogger>(relaxed = true)

    Given("kjerneregler") {
        When("en regel kaster en annen exception enn RegelException") {
            Then("exception propageres") {
                val kjerneRegel = mockk<Regel>()
                every { kjerneRegel.evaluer(any(), any()) } throws IllegalStateException("kjerne-feil")
                val motor = RegelMotor(
                    RegelSett(KJERNE_REGELTYPE to listOf(kjerneRegel)),
                     RegelSett(KOMPLETT_REGELTYPE to emptyList()),
                     logger)

                shouldThrow<IllegalStateException> {
                    motor.kjerneregler(ansatt, bruker)
                }
            }
        }
    }

    Given("kompletteRegler") {
        When("en regel kaster en annen exception enn RegelException") {
            Then("exception propageres") {
                val komplettRegel = mockk<Regel>()
                every { komplettRegel.evaluer(any(), any()) } throws IllegalStateException("komplett-feil")
                val motor = RegelMotor(RegelSett(KJERNE_REGELTYPE to emptyList()), RegelSett(KOMPLETT_REGELTYPE to listOf(komplettRegel)), logger)

                shouldThrow<IllegalStateException> {
                    motor.kompletteRegler(ansatt, bruker)
                }
            }
        }
    }
})
