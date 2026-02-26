package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import java.time.Instant
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class OppfølgingHendelse(
    @param:JsonAlias("oppfolgingsperiodeUuid")
    val id: UUID,
    val kontor: Kontor?,
    @param:JsonAlias("sisteEndringsType")
    val endringType: EndringType,
    val aktorId: AktørId,
    @param:JsonAlias("ident")
    val brukerId: BrukerId,
    val startTidspunkt: Instant) {

    enum class EndringType {
        ARBEIDSOPPFOLGINGSKONTOR_ENDRET,
        OPPFOLGING_STARTET,
        OPPFOLGING_AVSLUTTET
    }
    data class Kontor(val kontorId: Enhetsnummer, val kontorNavn: String = "Ukjent")
}