package no.nav.tilgangsmaskin.ansatt.nom

import java.time.LocalDate.EPOCH
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode.Companion.FUTURE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.pluralize
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(private val nom: Nom, private val handler: NomHendelseResultatLogger) {

    private val log = getLogger(javaClass)

    @KafkaListener(topics = ["#{'\${nom.topic}'}"], concurrency = "1", batch = "true", filter = "fnrFilterStrategy")
    fun listen(hendelser: List<NomHendelse>) {
        log.info("Mottok ${hendelser.size} ${"hendelse".pluralize(hendelser)}")
        hendelser.forEach {
            log.info("Behandler hendelse: {}", it)
            with(it) {
                runCatching {
                    nom.lagre(
                            NomAnsattData(
                                    AnsattId(navident), BrukerId(personident),
                                    NomAnsattPeriode(startdato ?: EPOCH, sluttdato ?: FUTURE)))
                }.fold(
                        { handler.ok(navident, personident) },
                        { handler.feilet(navident, personident, it) })
            }
        }
        log.info("${hendelser.size} ${"hendelse".pluralize(hendelser)} ferdig behandlet")
    }
}
