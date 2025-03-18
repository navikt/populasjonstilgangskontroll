package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(private val nom: NomTjeneste) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"])
    fun listen(hendelse : NomHendelse) {
       if (hendelse.navident != null && hendelse.personident != null) {
           log.info("Mottatt hendelse: {}", hendelse)
           runCatching {
                nom.lagre(hendelse.navident, hendelse.personident, hendelse.startdato,hendelse.sluttdato)
              }.onFailure {
                log.error("Feil ved lagring av hendelse: {} for ${hendelse.navident}", it.message, it)
              }.getOrNull()?.also {
                log.info("Lagret hendelse med id: ${it.id} for ${hendelse.navident}")
           }
       } else log.warn("Mottatt hendelse uten forventede felter: {}", hendelse)
    }
}