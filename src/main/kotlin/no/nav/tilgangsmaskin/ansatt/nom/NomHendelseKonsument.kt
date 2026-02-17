package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.ALLTID
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDate.EPOCH

@Component
class NomHendelseKonsument(private val nom: NomTjeneste, private val logger: NomHendelseLogger) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["org.nom.api-ressurs-state-v4"],
        properties = [
            "spring.json.trusted.packages=*",
            "spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.nom.NomHendelse",
            "spring.json.use.type.headers=false"
        ],
        groupId = $$"${spring.application.name}-nom",
        filter = "fnrFilterStrategy")
    fun listen(hendelser: List<NomHendelse>) {
        logger.start(hendelser)
        hendelser.forEach { hendelse ->
            logger.behandler(hendelse)
            runCatching {
                nom.lagre(
                    NomAnsattData(
                        AnsattId(hendelse.navident),
                        BrukerId(hendelse.personident),
                        NomAnsattPeriode(hendelse.startdato ?: EPOCH, hendelse.sluttdato ?: ALLTID)
                    )
                )
            }.onSuccess {
                logger.ok(hendelse.navident, hendelse.personident)
            }.onFailure {
                logger.feilet(hendelse.navident, hendelse.personident, it)
            }
        }
        logger.ferdig(hendelser)
    }
}
