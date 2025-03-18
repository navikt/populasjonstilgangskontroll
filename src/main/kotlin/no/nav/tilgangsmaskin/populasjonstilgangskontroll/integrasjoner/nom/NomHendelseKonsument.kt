package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
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
               validate(navident, personident)?.let {
                   nom.lagre(it.first, it.second, startdato,sluttdato)
               }
              }.onFailure {
                log.error("Kunne ikke lagre hendelse: {} for $navident", it.message, it)
              }.onSuccess {
                log.info("Lagret fødselsnummer for $navident OK")
           }
        }
    }
    private fun validate(ansattId: String, brukerId: String) = Pair(AnsattId(ansattId),BrukerId(brukerId))
}