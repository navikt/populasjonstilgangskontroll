package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import kotlin.jvm.javaClass

@Component
class `OppfølgingHendelseKonsument` {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
         properties = ["spring.json.value.default.type=kotlin.Any"],
        groupId = $$"${spring.application.name}-opp")
    fun listen(hendelse: Any) {
        log.info("Mottok oppfølginghendelse: $hendelse")
    }
}
