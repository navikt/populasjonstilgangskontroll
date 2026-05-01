package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType
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

class OppfølgingHendelseKonsumentTest : BehaviorSpec() {

    private val oppfølging = mockk<OppfølgingTjeneste>(relaxed = true)
    private val konsument = OppfølgingHendelseKonsument(oppfølging)

    init {
        beforeEach {
            clearMocks(oppfølging)
        }

        Given("hendelse av type $OPPFOLGING_STARTET") {
            When("hendelse mottas") {
                Then("registrer med riktige argumenter") {
                    konsument.listen(hendelse(OPPFOLGING_STARTET))
                    verify {
                        oppfølging.registrer(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, START_TIDSPUNKT)
                    }
                }
            }
        }

        Given("hendelse av type $ARBEIDSOPPFOLGINGSKONTOR_ENDRET") {
            When("hendelse mottas") {
                Then("registrer med riktige argumenter") {
                    konsument.listen(hendelse(ARBEIDSOPPFOLGINGSKONTOR_ENDRET))
                    verify {
                        oppfølging.registrer(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, START_TIDSPUNKT)
                    }
                }
            }
        }

        Given("hendelse av type $OPPFOLGING_AVSLUTTET") {
            When("hendelse mottas") {
                Then("avslutter med riktige argumenter") {
                    konsument.listen(hendelse(OPPFOLGING_AVSLUTTET, null, Instant.now()))

                    verify {
                        oppfølging.avslutt(ID, Identer(BRUKER_ID, AKTOR_ID))
                    }
                }
            }
        }
    }

    private fun hendelse(type: EndringType, kontor: Kontor? = KONTOR, sluttTidspunkt: Instant? = null) =
        OppfølgingHendelse(kontor, type, ID, AKTOR_ID, BRUKER_ID, START_TIDSPUNKT, sluttTidspunkt, PRODUCER_TIMESTAMP)

    companion object {
        private val ID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val BRUKER_ID = BrukerId("08526835670")
        private val AKTOR_ID = AktørId("1234567890123")
        private val KONTOR = Kontor(Enhetsnummer("0301"), "NAV Oslo")
        private val START_TIDSPUNKT = Instant.parse("2024-01-01T09:00:00Z")
        private val PRODUCER_TIMESTAMP = Instant.parse("2024-01-01T09:00:01Z")
    }
}
