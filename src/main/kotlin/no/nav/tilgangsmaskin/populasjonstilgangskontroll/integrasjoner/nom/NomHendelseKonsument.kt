package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.DateRange
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.pluralize
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.adapter.RecordFilterStrategy
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDate.EPOCH
import java.time.LocalDate.MAX

@Component
class NomHendelseKonsument(private val nom: NomOperasjoner, private val handler: EventResultHandler) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"], concurrency = "5", batch = "true", filter = "fnrFilterStrategy")
    fun listen(hendelser: List<NomHendelse>) {
        log.info("Mottok ${hendelser.size} ${"hendelse".pluralize(hendelser)}")
        hendelser.forEach {
            log.info("Behandler hendelse: {}", it)
            with(it) {
                runCatching {
                    nom.lagre(NomAnsattData(AnsattId(navident), BrukerId(personident), DateRange(startdato?: EPOCH, sluttdato?: MAX)))
                }.fold(
                    onSuccess = { handler.handleOK(navident, personident) },
                    onFailure = { handler.handleFailure(navident, personident, it) }
                )
            }
        }
        log.info("${hendelser.size} ${"hendelse".pluralize(hendelser)} ferdig behandlet")
    }
    data class NomAnsattData(val ansattId: AnsattId, val brukerId: BrukerId, val gyldighet: ClosedRange<LocalDate> = EPOCH..MAX)
}

@Component
class FnrFilterStrategy: RecordFilterStrategy<String, NomHendelse> {
    private val log = getLogger(FnrFilterStrategy::class.java)
    override fun filter(record: ConsumerRecord<String, NomHendelse>) =
        runCatching { BrukerId(record.value().personident) }.isFailure.also {
            if (it) log.warn("Ugyldig personident: ${record.value().personident} ble filtrert bort")
        }

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