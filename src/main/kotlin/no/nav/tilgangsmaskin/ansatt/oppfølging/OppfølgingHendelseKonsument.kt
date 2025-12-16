package no.nav.tilgangsmaskin.ansatt.oppfølging

import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import kotlin.jvm.javaClass

@Component
class `OppfølgingHendelseKonsument` {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = $$"${spring.application.name}-opp")
    fun listen(hendelse: OppfølgingHendelse) {
        log.info("Mottok oppfølginghendelse: $hendelse")
    }
}

data class OppfølgingHendelse(
    val kontor: Kontor,
    val sisteEndringsType: String,
    val oppfolgingsperiodeUuid: String,
    val aktorId: String,
    val ident: String,
    val startTidspunkt: String,
    val sluttTidspunkt: String?, // Nullable, always null on start messages
    val producerTimestamp: String
)

data class Kontor(
    val kontorId: String,
    val kontorNavn: String
)