package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
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
import org.springframework.kafka.annotation.KafkaListener
import java.time.Instant
import java.util.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

class OppfølgingHendelseKonsumentTest : BehaviorSpec({

    val oppfølging = mockk<OppfølgingTjeneste>(relaxed = true)
    val konsument = OppfølgingHendelseKonsument(oppfølging)

    fun hendelse(type: OppfølgingHendelse.EndringType, kontor: Kontor? = KONTOR, sluttTidspunkt: Instant? = null) = OppfølgingHendelse(
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
            Then("kalles registrer med Startet-domeneobjekt") {
                konsument.listen(hendelse(OPPFOLGING_STARTET))
                verify {
                    oppfølging.registrer(Oppfølgingsendring.Startet(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, START_TIDSPUNKT))
                }
            }
        }
    }

    Given("ARBEIDSOPPFOLGINGSKONTOR_ENDRET") {
        When("listen kalles") {
            Then("kalles registrer med KontorEndret-domeneobjekt") {
                konsument.listen(hendelse(ARBEIDSOPPFOLGINGSKONTOR_ENDRET))
                verify {
                    oppfølging.registrer(Oppfølgingsendring.KontorEndret(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, START_TIDSPUNKT))
                }
            }
        }
    }

    Given("OPPFOLGING_AVSLUTTET") {
        When("listen kalles") {
            Then("kalles avslutt med Avsluttet-domeneobjekt") {
                konsument.listen(hendelse(OPPFOLGING_AVSLUTTET,  null,  Instant.now()))

                verify {
                    oppfølging.avslutt(Oppfølgingsendring.Avsluttet(ID, Identer(BRUKER_ID, AKTOR_ID)))
                }
            }
        }
    }

    Given("@KafkaListener-konfigurasjon") {
        When("default-type-property leses fra annotasjonen") {
            Then("matcher faktisk klassenavn for OppfølgingHendelse") {
                val listen = OppfølgingHendelseKonsument::class.functions.first { it.name == "listen" }
                val annotasjon = listen.findAnnotation<KafkaListener>()!!
                val defaultType = annotasjon.properties
                    .first { it.startsWith("spring.json.value.default.type=") }
                    .substringAfter("=")

                defaultType shouldBe OppfølgingHendelse::class.java.name
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
