package no.nav.tilgangsmaskin.ansatt.nom

import java.time.LocalDate.EPOCH
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.ALLTID
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(private val nom: Nom, private val log: NomHendelseLogger) {


    @KafkaListener(topics = ["#{'\${nom.topic}'}"], concurrency = "1", batch = "true", filter = "fnrFilterStrategy")
    fun listen(hendelser: List<NomHendelse>) {
        log.start(hendelser)
        hendelser.forEach {
            log.behandler(it)
            with(it) {
                runCatching {
                    nom.lagre(
                            NomAnsattData(
                                    AnsattId(navident), BrukerId(personident),
                                    NomAnsattPeriode(startdato ?: EPOCH, sluttdato ?: ALLTID)))
                }.fold(
                        { log.ok(navident, personident) },
                        { log.feilet(navident, personident, it) })
            }
        }
        log.ferdig(hendelser)
    }
}
