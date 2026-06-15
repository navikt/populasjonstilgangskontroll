package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingEndring.Avsluttet
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingEndring.StartetEllerEndret
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Identer
import java.time.Instant
import java.util.UUID

class OppfølgingMappingTest : BehaviorSpec({

    Given("OPPFOLGING_STARTET med kontor") {
        Then("mapper til MedKontor") {
            hendelse(OPPFOLGING_STARTET, KONTOR).tilDomene() shouldBe StartetEllerEndret(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, TIDSPUNKT, OPPFOLGING_STARTET)
        }
    }

    Given("ARBEIDSOPPFOLGINGSKONTOR_ENDRET med kontor") {
        Then("mapper til MedKontor") {
            hendelse(ARBEIDSOPPFOLGINGSKONTOR_ENDRET, KONTOR).tilDomene() shouldBe StartetEllerEndret(ID, Identer(BRUKER_ID, AKTOR_ID), KONTOR, TIDSPUNKT, ARBEIDSOPPFOLGINGSKONTOR_ENDRET)
        }
    }

    Given("OPPFOLGING_AVSLUTTET") {
        Then("mapper til Avsluttet uavhengig av kontor") {
            hendelse(OPPFOLGING_AVSLUTTET).tilDomene().shouldBeInstanceOf<Avsluttet>()
        }
    }

    Given("OPPFOLGING_STARTET uten kontor") {
        Then("kaster IllegalArgumentException med kontekstuell melding") {
             shouldThrow<IllegalArgumentException> {
                hendelse(OPPFOLGING_STARTET).tilDomene()
            }
        }
    }

    Given("ARBEIDSOPPFOLGINGSKONTOR_ENDRET uten kontor") {
        Then("kaster IllegalArgumentException med kontekstuell melding") {
            shouldThrow<IllegalArgumentException> {
                hendelse(ARBEIDSOPPFOLGINGSKONTOR_ENDRET).tilDomene()
            }
        }
    }
}) {
    companion object {
        private fun hendelse(type: EndringType, kontor: Kontor? = null) =
            OppfølgingHendelse(
                kontor, type, ID, AKTOR_ID, BRUKER_ID,TIDSPUNKT, if (type == OPPFOLGING_AVSLUTTET) Instant.now() else null,
                PRODUCER_TIMESTAMP,
            )
        private val ID = UUID.randomUUID()
        private val BRUKER_ID = BrukerId("08526835670")
        private val AKTOR_ID = AktørId("1234567890123")
        private val KONTOR = Kontor(Enhetsnummer("0301"), "NAV Oslo")
        private val TIDSPUNKT = Instant.parse("2024-01-01T09:00:00Z")
        private val PRODUCER_TIMESTAMP = Instant.parse("2024-01-01T09:00:01Z")
    }
}

