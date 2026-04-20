package no.nav.tilgangsmaskin.ansatt.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.ALLTID
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDate.EPOCH

@Component
class NomHendelseKonsument(private val nom: NomTjeneste) {

    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [NOM_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.nom.NomHendelse"],
        groupId = NOM,
        errorHandler = NOM_ERROR_HANDLER,
        filter = NOM_FNR_FILTER_STRATEGY)
    fun listen(hendelser: List<NomHendelse>) {
        log.info("Mottok ${hendelser.size} hendelse(r) fra NOM")
        hendelser.forEach { h ->
            log.trace("Behandler hendelse fra NOM: {}", h)
            nom.lagre(h.ansattData())
            log.trace("Lagret brukerId ${h.personident.maskFnr()} for ${h.navident} OK")
        }
        log.info("${hendelser.size} hendelse(r) fra NOM ferdig behandlet og lagret")
    }

    private fun NomHendelse.ansattData() =
        NomAnsattData(
            AnsattId(navident),
            BrukerId(personident),
            NomAnsattPeriode(startdato ?: EPOCH, sluttdato ?: ALLTID)
        )
    companion object {
        const val NOM_ERROR_HANDLER = "nomErrorHandler"
        const val NOM_FNR_FILTER_STRATEGY = "nomFnrFilterStrategy"
        private const val NOM_TOPIC = "org.nom.api-ressurs-state-v4"
    }
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(val personident: String, val navident: String, val startdato: LocalDate?, val sluttdato: LocalDate?) {

    @Generated
    override fun toString() =
        "${javaClass.simpleName} (personident=${personident.maskFnr()}, navident=$navident, startdato=$startdato, sluttdato=$sluttdato)"
}