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
    val konsument = NomHendelseKonsument(nom)

    Given("konsumering av NOM-hendelser") {
        When("listen kalles med hendelse") {
            Then("lagrer ansatt for hendelse i listen") {
                val hendelse = hendelse()
                konsument.listen(hendelse,0L,0)

                verify(exactly = 1) { nom.lagre(any()) }
            }
        }

        When("hendelse har alle felter satt") {
            Then("mapper hendelse til NomAnsattData med korrekte felter") {
                konsument.listen(hendelse(NAVIDENT,PERSONIDENT,STARTDATO,SLUTTDATO),0L,0)

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
                konsument.listen(hendelse(startdato = null),0L,0)
                verify {
                    nom.lagre(match { it.gyldighet.start == EPOCH })
                }
            }
        }

        When("sluttdato er null") {
            Then("bruker ALLTID som sluttdato") {
                konsument.listen(hendelse(sluttdato = null),0L,0)
                verify {
                    nom.lagre(match { it.gyldighet.endInclusive == ALLTID })
                }
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
