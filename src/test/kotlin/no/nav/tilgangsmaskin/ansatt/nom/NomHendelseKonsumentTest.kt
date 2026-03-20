package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.ALLTID
import java.time.LocalDate
import java.time.LocalDate.EPOCH

class NomHendelseKonsumentTest : DescribeSpec({

    val nom = mockk<NomTjeneste>(relaxed = true)
    val logger = mockk<NomHendelseLogger>(relaxed = true)
    val konsument = NomHendelseKonsument(nom, logger)

    fun hendelse(navident: String = NAVIDENT, personident: String = PERSONIDENT, startdato: LocalDate? = STARTDATO, sluttdato: LocalDate? = SLUTTDATO) =
        NomHendelse(personident, navident, startdato, sluttdato)

    describe("listen") {

        it("lagrer ansatt for hver hendelse i listen") {
            val hendelser = listOf(hendelse(), hendelse(navident = "Z888888", personident = "20478606614"))

            konsument.listen(hendelser)

            verify(exactly = 2) { nom.lagre(any()) }
        }

        it("mapper hendelse til NomAnsattData med korrekte felter") {
            konsument.listen(listOf(hendelse()))

            verify {
                nom.lagre(NomAnsattData(
                    AnsattId(NAVIDENT),
                    BrukerId(PERSONIDENT),
                    NomAnsattPeriode(STARTDATO, SLUTTDATO)
                ))
            }
        }

        it("bruker EPOCH som startdato når startdato er null") {
            konsument.listen(listOf(hendelse(startdato = null)))

            verify {
                nom.lagre(match { it.gyldighet.start == EPOCH })
            }
        }

        it("bruker ALLTID som sluttdato når sluttdato er null") {
            konsument.listen(listOf(hendelse(sluttdato = null)))

            verify {
                nom.lagre(match { it.gyldighet.endInclusive == ALLTID })
            }
        }

        it("logger start og ferdig for hele batchen") {
            val hendelser = listOf(hendelse())
            konsument.listen(hendelser)
            verify { logger.start(hendelser) }
            verify { logger.ferdig(hendelser) }
        }

        it("logger ok for hver vellykket hendelse") {
            konsument.listen(listOf(hendelse()))
            verify { logger.ok(NAVIDENT, PERSONIDENT) }
        }

        it("logger feilet og fortsetter med neste hendelse når lagre kaster") {
            val annenHendelse = hendelse(navident = "Z888888", personident = "20478606614")
            every { nom.lagre(match { it.ansattId == AnsattId(NAVIDENT) }) } throws RuntimeException("DB-feil")

            konsument.listen(listOf(hendelse(), annenHendelse))

            verify { logger.feilet(eq(NAVIDENT), eq(PERSONIDENT), any()) }
            verify { nom.lagre(match { it.ansattId == AnsattId("Z888888") }) }
            verify { logger.ok("Z888888", "20478606614") }
        }
    }
}) {
    companion object {
        private const val NAVIDENT = "Z999999"
        private const val PERSONIDENT = "08526835670"
        private val STARTDATO = LocalDate.of(2023, 1, 1)
        private val SLUTTDATO = LocalDate.of(2025, 12, 31)
    }
}




