package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(private val nom: NomTjeneste) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"])
    fun listen(hendelse : NomHendelse) {
        with(hendelse) {
           log.info("Mottatt hendelse: {}", this)
           runCatching {
                nom.lagre(navident, personident, startdato,sluttdato)
              }.onFailure {
                log.error("Feil ved lagring av hendelse: {} for $navident", it.message, it)
              }.getOrNull()?.also {
                log.info("Lagret hendelse med id: ${it.id} for $navident")
           }
        }
    }
}