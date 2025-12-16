package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID
import kotlin.jvm.javaClass

@Component
class `OppfølgingHendelseKonsument` {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = "$OPPFØLGING-hendelse-konsument")
    fun listen(hendelse: OppfølgingHendelse) {
        log.info("Mottok oppfølginghendelse: $hendelse")
    }
}

data class OppfølgingHendelse(
    val kontor: Kontor,
    val sisteEndringsType: EndringType,
    val oppfolgingsperiodeUuid: UUID,
    val aktorId: AktørId,
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

