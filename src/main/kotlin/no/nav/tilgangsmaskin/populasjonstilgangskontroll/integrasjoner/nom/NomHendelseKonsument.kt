package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(private val nom: NomTjeneste, private val successHandler: SuccessHandler,private val failureHandler: FailureHandler) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"])
    fun listen(hendelse : NomHendelse) {
        with(hendelse) {
           log.info("Mottatt hendelse: {}", this)
           runCatching {
               validate(navident, personident).let {
                   nom.lagre(it.first, it.second, startdato,sluttdato)
               }
              }.onFailure {
                  failureHandler.handle(navident,personident,it)
              }.onSuccess {
                 successHandler.handle(navident, personident)
           }
        }
    }
    private fun validate(ansattId: String, brukerId: String) = Pair(AnsattId(ansattId),BrukerId(brukerId))
}

@Component
@Counted
class SuccessHandler {
    private val log = getLogger(SuccessHandler::class.java)
    fun handle(ansattId: String, brukerId: String)  {
        log.info("Lagret fødselsnummer ${brukerId.mask()} for $ansattId OK")
    }
}
@Component
@Counted
class FailureHandler {
    private val log = getLogger(FailureHandler::class.java)
    fun handle(ansattId: String, brukerId: String, e: Throwable)  {
        log.error("Kunne ikke lagre fødselsnummer ${brukerId.mask()} for $ansattId (${e.message})", e)
    }
}