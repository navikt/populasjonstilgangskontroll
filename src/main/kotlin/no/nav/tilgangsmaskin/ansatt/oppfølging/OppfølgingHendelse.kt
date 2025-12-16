package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import java.time.Instant
import java.util.UUID

data class OppfølgingHendelse(
    val kontor: Kontor?,
    val sisteEndringsType: EndringType,
    val oppfolgingsperiodeUuid: UUID,
    val aktorId: `AktørId`,
    val ident: BrukerId,
    val startTidspunkt: Instant,
    val sluttTidspunkt: Instant?, // Nullable, always null on start messages
    val producerTimestamp: Instant) {

    enum class EndringType {
        ARBEIDSOPPFOLGINGSKONTOR_ENDRET,
        OPPFOLGING_STARTET,
        OPPFOLGING_AVSLUTTET
    }
    data class Kontor(val kontorId: Enhetsnummer, val kontorNavn: String)
}