package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.Identer
import java.time.Instant
import java.util.UUID

sealed interface OppfølgingEndring {
    val uuid: UUID
    val identer: Identer

    data class StartetEllerEndret(
        override val uuid: UUID,
        override val identer: Identer,
        val kontor: Kontor,
        val tidspunkt: Instant,
        val type: EndringType,
    ) : OppfølgingEndring

    data class Avsluttet(
        override val uuid: UUID,
        override val identer: Identer,
    ) : OppfølgingEndring
}


