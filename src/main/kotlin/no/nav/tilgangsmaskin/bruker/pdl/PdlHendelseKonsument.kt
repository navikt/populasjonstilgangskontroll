package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.person.pdl.leesah.Personhendelse
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PdlHendelseKonsument {
    private val log = getLogger(javaClass)

    @KafkaListener(topics = [ "pdl.leesah-v1"], containerFactory = "pdlKafkaListenerContainerFactory")
    fun listen(hendelse: Personhendelse) {
        log.info("Mottok hendelse fra PDL $hendelse" )
    }
}