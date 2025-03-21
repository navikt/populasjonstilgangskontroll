package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.pluralize
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDate.EPOCH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomAnsattData.NomAnsattPeriode.Companion.FOREVER

@Component
class NomHendelseKonsument(private val nom: NomOperasjoner, private val handler: NomEventResultHandler) {

    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"], concurrency = "5", batch = "true", filter = "fnrFilterStrategy")
    fun listen(hendelser: List<NomHendelse>) {
        log.info("Mottok ${hendelser.size} ${"hendelse".pluralize(hendelser)}")
        hendelser.forEach {
            log.info("Behandler hendelse: {}", it)
            with(it) {
                runCatching {
                    nom.lagre(NomAnsattData(AnsattId(navident), BrukerId(personident), NomAnsattPeriode(startdato ?: EPOCH, sluttdato ?: FOREVER)
                    ))
                }.fold(
                    onSuccess = { handler.handleOK(navident, personident) },
                    onFailure = { handler.handleFailure(navident, personident, it) }
                )
            }
        }
        log.info("${hendelser.size} ${"hendelse".pluralize(hendelser)} ferdig behandlet")
    }
}
