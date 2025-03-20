package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.adapter.RecordFilterStrategy
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(private val nom: NomOperasjoner, private val handler: EventResultHandler) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"], concurrency = "5", batch = "true", filter = "fnrFilterStrategy")
    fun listen(hendelser: List<NomHendelse>) {
        log.info("Mottok ${hendelser.size} hendelser")
        hendelser.forEach { hendelse ->
            log.info("Behandler hendelse: {}", hendelse)
            runCatching {
                nom.lagre(AnsattId(hendelse.navident), BrukerId(hendelse.personident), hendelse.startdato, hendelse.sluttdato)
            }.fold(
                onSuccess = { handler.handleOK(hendelse.navident, hendelse.personident) },
                onFailure = { handler.handleFailure(hendelse.navident, hendelse.personident, it) }
            )
        }
        log.info("${hendelser.size} hendelser ferdig behandlet")
    }
}

@Component
class FnrFilterStrategy: RecordFilterStrategy<String, NomHendelse> {
    private val log = getLogger(FnrFilterStrategy::class.java)
    override fun filter(record: ConsumerRecord<String, NomHendelse>) = skalFiltres(record.value().personident).also {
        if (it) log.warn("Ugyldig personident: ${record.value().personident} ble filtrert bort")
    }
    fun skalFiltres(ident: String) = runCatching { BrukerId(ident) }.isFailure
}
@Component
@Counted
class EventResultHandler {
    private val log = getLogger(EventResultHandler::class.java)
    fun handleOK(ansattId: String, brukerId: String)  {
        log.info("Lagret fødselsnummer ${brukerId.mask()} for $ansattId OK")
    }
    fun handleFailure(ansattId: String, brukerId: String, e: Throwable)  {
        log.error("Kunne ikke lagre fødselsnummer ${brukerId.mask()} for $ansattId (${e.message})", e)
    }
}