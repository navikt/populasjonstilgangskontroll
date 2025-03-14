package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"])
    fun listen(hendelse : NomHendelse) {
        if (hendelse.ansattId != null) {
            log.info("Mottatt hendelse: $hendelse")
        }
    }
}