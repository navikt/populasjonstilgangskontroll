package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import java.time.Instant
import java.util.*

data class OppfølgingHendelse(
    val kontor: Kontor?,
    val sisteEndringsType: EndringType,
    val oppfolgingsperiodeUuid: UUID,
    val aktorId: AktørId,
    val ident: BrukerId,
    val startTidspunkt: Instant,
    val sluttTidspunkt: Instant?,
    val producerTimestamp: Instant) {

    enum class EndringType {
        ARBEIDSOPPFOLGINGSKONTOR_ENDRET,
        OPPFOLGING_STARTET,
        OPPFOLGING_AVSLUTTET
    }

    data class Kontor @JsonCreator constructor(
        @JsonProperty("kontorId") kontorId: String,
        @JsonProperty("kontorNavn") val kontorNavn: String = "Ukjent"
    ) {
        val kontorId: Enhetsnummer = Enhetsnummer(kontorId)
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun create(
            @JsonProperty("kontor") kontor: Kontor?,
            @JsonProperty("sisteEndringsType") sisteEndringsType: EndringType,
            @JsonProperty("oppfolgingsperiodeUuid") oppfolgingsperiodeUuid: UUID,
            @JsonProperty("aktorId") aktorId: String,
            @JsonProperty("ident") ident: String,
            @JsonProperty("startTidspunkt") startTidspunkt: Instant,
            @JsonProperty("sluttTidspunkt") sluttTidspunkt: Instant?,
            @JsonProperty("producerTimestamp") producerTimestamp: Instant
        ) = OppfølgingHendelse(
            kontor = kontor,
            sisteEndringsType = sisteEndringsType,
            oppfolgingsperiodeUuid = oppfolgingsperiodeUuid,
            aktorId = AktørId(aktorId),
            ident = BrukerId(ident),
            startTidspunkt = startTidspunkt,
            sluttTidspunkt = sluttTidspunkt,
            producerTimestamp = producerTimestamp
        )
    }
}