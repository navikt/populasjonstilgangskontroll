package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@ConditionalOnNotProd
class NomHendelseKonsument(private val nom: NomTjeneste) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"])
    fun listen(hendelse : NomHendelse) {
       if (hendelse.navident != null) {
           log.info("Mottatt hendelse: {}", hendelse)
           runCatching {
                nom.lagre(AnsattId(hendelse.navident), BrukerId(hendelse.navident))
              }.onFailure {
                log.error("Feil ved lagring av hendelse: {}", it.message)
              }.getOrNull()?.also {
                log.info("Lagret hendelse med id: {}", it.id)
           }
       }
    }
}