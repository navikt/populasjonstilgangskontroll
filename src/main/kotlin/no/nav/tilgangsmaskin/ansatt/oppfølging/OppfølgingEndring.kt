package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.Identer
import java.time.Instant
import java.util.UUID

sealed interface OppfølgingEndring {
    val uuid: UUID
    val identer: Identer

    /** Felles type for hendelser som har et kontor — brukes som ett-arg signatur i tjenestelaget. */
    sealed interface MedKontor : OppfølgingEndring {
        val kontor: Kontor
        val tidspunkt: Instant
    }

    data class Startet(
        override val uuid: UUID,
        override val identer: Identer,
        override val kontor: Kontor,
        override val tidspunkt: Instant,
    ) : MedKontor

    data class KontorEndret(
        override val uuid: UUID,
        override val identer: Identer,
        override val kontor: Kontor,
        override val tidspunkt: Instant,
    ) : MedKontor

    data class Avsluttet(
        override val uuid: UUID,
        override val identer: Identer,
    ) : OppfølgingEndring
}


