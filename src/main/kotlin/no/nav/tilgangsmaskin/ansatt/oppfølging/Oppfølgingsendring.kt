package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Avsluttet
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.KontorEndret
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Startet
import no.nav.tilgangsmaskin.bruker.Identer
import java.time.Instant
import java.util.UUID

sealed interface Oppfølgingsendring {
    val uuid: UUID
    val identer: Identer

    /** Felles type for hendelser som har et kontor — brukes som ett-arg signatur i tjenestelaget. */
    sealed interface MedKontor : Oppfølgingsendring {
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
    ) : Oppfølgingsendring
}

fun OppfølgingHendelse.tilDomene(): Oppfølgingsendring {
    val identer = Identer(ident, aktorId)
    fun krevKontor() = requireNotNull(kontor) {
        "kontor mangler for $sisteEndringsType (uuid=$oppfolgingsperiodeUuid)"
    }
    return when (sisteEndringsType) {
        OPPFOLGING_STARTET -> Startet(oppfolgingsperiodeUuid, identer, krevKontor(), startTidspunkt)
        ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> KontorEndret(oppfolgingsperiodeUuid, identer, krevKontor(), startTidspunkt)
        OPPFOLGING_AVSLUTTET -> Avsluttet(oppfolgingsperiodeUuid, identer)
    }
}

