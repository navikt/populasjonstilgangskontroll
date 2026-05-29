package no.nav.tilgangsmaskin.ansatt.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.ALLTID
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.KafkaHeaders.OFFSET
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDate.EPOCH

@Component
class NomHendelseKonsument(private val nom: NomTjeneste) {

    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [NOM_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.nom.NomHendelse"],
        groupId = NOM, filter = NOM_FNR_FILTER_STRATEGY)
    fun listen(hendelse: NomHendelse,
               @Header(OFFSET) offset: Long,
               @Header(RECEIVED_PARTITION) partition: Int) =
        with(hendelse.ansattData()) {
            log.info("Behandler hendelse $hendelse for $ansattId fra NOM, partition $partition og offset $offset")
            nom.lagre(this)
            log.info("Lagret brukerId $brukerId for $ansattId, partition $partition og offset $offset OK")
            log.info("$ansattId hendelse på partition $partition, offset $offset fra NOM ferdig behandlet og lagret")
    }

    private fun NomHendelse.ansattData() =
        NomAnsattData(AnsattId(navident), BrukerId(personident), NomAnsattPeriode(startdato ?: EPOCH, sluttdato ?: ALLTID))

    companion object {
        const val NOM_FNR_FILTER_STRATEGY = "nomFnrFilterStrategy"
        private const val NOM_TOPIC = "org.nom.api-ressurs-state-v4"
    }
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(val personident: String, val navident: String,
                       val startdato: LocalDate?, val sluttdato: LocalDate?) {

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} (personident=${personident.maskFnr()}, navident=$navident, startdato=$startdato, sluttdato=$sluttdato)"
}