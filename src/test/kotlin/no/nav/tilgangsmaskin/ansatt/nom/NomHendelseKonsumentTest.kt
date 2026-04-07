package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.ALLTID
import java.time.LocalDate
import java.time.LocalDate.EPOCH

class NomHendelseKonsumentTest : BehaviorSpec({

    val nom = mockk<NomTjeneste>(relaxed = true)
    val logger = mockk<NomHendelseLogger>(relaxed = true)
    val konsument = NomHendelseKonsument(nom, logger)

    Given("listen") {
        When("listen kalles med flere hendelser") {
            Then("lagrer ansatt for hver hendelse i listen") {
                val hendelser = listOf(hendelse(), hendelse("Z888888", "20478606614"))
                konsument.listen(hendelser)

                verify(exactly = 2) { nom.lagre(any()) }
            }
        }

        When("hendelse har alle felter satt") {
            Then("mapper hendelse til NomAnsattData med korrekte felter") {
                konsument.listen(listOf(hendelse(NAVIDENT,PERSONIDENT,STARTDATO,SLUTTDATO)))

                verify {
                    nom.lagre(NomAnsattData(
                        AnsattId(NAVIDENT),
                        BrukerId(PERSONIDENT),
                        NomAnsattPeriode(STARTDATO, SLUTTDATO)
                    ))
                }
            }
        }

        When("startdato er null") {
            Then("bruker EPOCH som startdato") {
                konsument.listen(listOf(hendelse(startdato = null)))

                verify {
                    nom.lagre(match { it.gyldighet.start == EPOCH })
                }
            }
        }

        When("sluttdato er null") {
            Then("bruker ALLTID som sluttdato") {
                konsument.listen(listOf(hendelse(sluttdato = null)))

                verify {
                    nom.lagre(match { it.gyldighet.endInclusive == ALLTID })
                }
            }
        }

        When("listen kalles") {
            Then("logger start og ferdig for hele batchen") {
                val hendelser = listOf(hendelse())
                konsument.listen(hendelser)

                verify { logger.start(hendelser) }
                verify { logger.ferdig(hendelser) }
            }

            Then("logger ok for hver vellykket hendelse") {
                konsument.listen(listOf(hendelse()))

                verify { logger.ok(NAVIDENT, PERSONIDENT) }
            }
        }

        When("lagre kaster exception") {
            Then("logger feilet og fortsetter med neste hendelse") {
                val annenHendelse = hendelse("Z888888", "20478606614")
                every { nom.lagre(match { it.ansattId == AnsattId(NAVIDENT) }) } throws RuntimeException("DB-feil")

                konsument.listen(listOf(hendelse(), annenHendelse))

                verify { logger.feilet(eq(NAVIDENT), eq(PERSONIDENT), any()) }
                verify { nom.lagre(match { it.ansattId == AnsattId("Z888888") }) }
                verify { logger.ok("Z888888", "20478606614") }
            }
        }
    }
}) {
    companion object {
        private fun hendelse(navident: String = NAVIDENT, personident: String = PERSONIDENT, startdato: LocalDate? = STARTDATO, sluttdato: LocalDate? = SLUTTDATO) =
            NomHendelse(personident, navident, startdato, sluttdato)
        private const val NAVIDENT = "Z999999"
        private const val PERSONIDENT = "08526835670"
        private val STARTDATO = LocalDate.of(2023, 1, 1)
        private val SLUTTDATO = LocalDate.of(2025, 12, 31)
    }
}
