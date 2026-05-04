package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Identer
import java.time.Instant
import java.util.*

class OppfølgingHendelseKonsumentTest : BehaviorSpec({

    val oppfølging = mockk<OppfølgingTjeneste>(relaxed = true)
    val konsument = OppfølgingHendelseKonsument(oppfølging)

    fun hendelse(
        type: OppfølgingHendelse.EndringType,
        kontor: Kontor? = KONTOR,
        sluttTidspunkt: Instant? = null,
    ) = OppfølgingHendelse(
        kontor,
        type,
        ID,
        AKTOR_ID,
        BRUKER_ID,
        START_TIDSPUNKT,
        sluttTidspunkt,
        PRODUCER_TIMESTAMP,
    )

    beforeEach { clearMocks(oppfølging) }

    Given("OPPFOLGING_STARTET") {
        When("listen kalles") {
            Then("kalles registrer med riktige argumenter") {
                konsument.listen(hendelse(OPPFOLGING_STARTET))

                verify {
                    oppfølging.registrer(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, START_TIDSPUNKT)
                }
            }
            Then("kalles ikke avslutt") {
                konsument.listen(hendelse(OPPFOLGING_STARTET))

                verify(exactly = 0) { oppfølging.avslutt(any(), any()) }
            }
        }
    }

    Given("ARBEIDSOPPFOLGINGSKONTOR_ENDRET") {
        When("listen kalles") {
            Then("kalles registrer med riktige argumenter") {
                konsument.listen(hendelse(ARBEIDSOPPFOLGINGSKONTOR_ENDRET))

                verify {
                    oppfølging.registrer(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, START_TIDSPUNKT)
                }
            }
            Then("kalles ikke avslutt") {
                konsument.listen(hendelse(ARBEIDSOPPFOLGINGSKONTOR_ENDRET))

                verify(exactly = 0) { oppfølging.avslutt(any(), any()) }
            }
        }
    }

    Given("OPPFOLGING_AVSLUTTET") {
        When("listen kalles") {
            Then("kalles avslutt med riktige argumenter") {
                konsument.listen(hendelse(OPPFOLGING_AVSLUTTET, kontor = null, sluttTidspunkt = Instant.now()))

                verify {
                    oppfølging.avslutt(ID, Identer(BRUKER_ID, AKTOR_ID))
                }
            }
            Then("kalles ikke registrer") {
                konsument.listen(hendelse(OPPFOLGING_AVSLUTTET, kontor = null, sluttTidspunkt = Instant.now()))

                verify(exactly = 0) { oppfølging.registrer(any(), any(), any(), any()) }
            }
        }
    }
}) {
    companion object {
        private val ID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val BRUKER_ID = BrukerId("08526835670")
        private val AKTOR_ID = AktørId("1234567890123")
        private val KONTOR = Kontor(Enhetsnummer("0301"), "NAV Oslo")
        private val START_TIDSPUNKT = Instant.parse("2024-01-01T09:00:00Z")
        private val PRODUCER_TIMESTAMP = Instant.parse("2024-01-01T09:00:01Z")
    }
}
