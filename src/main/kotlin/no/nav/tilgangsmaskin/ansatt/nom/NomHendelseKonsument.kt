package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
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
        topics = [NOM_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.nom.NomHendelse"],
        groupId = NOM,
        filter = NOM_FNR_FILTER_STRATEGY)
    fun listen(hendelser: List<NomHendelse>) {
        logger.start(hendelser)
        hendelser.forEach { hendelse ->
            logger.behandler(hendelse)
            runCatching {
                nom.lagre(ansattFra(hendelse))
            }.onSuccess {
                logger.ok(hendelse.navident, hendelse.personident)
            }.onFailure {
                logger.feilet(hendelse.navident, hendelse.personident, it)
            }
        }
        logger.ferdig(hendelser)
    }

    private fun ansattFra(hendelse: NomHendelse): NomAnsattData =
       with(hendelse) {
           NomAnsattData(
               AnsattId(navident),
               BrukerId(personident),
               NomAnsattPeriode(startdato ?: EPOCH, sluttdato ?: ALLTID)
           )
       }
companion object {
    const val NOM_FNR_FILTER_STRATEGY = "nomFnrFilterStrategy"
    private const val NOM_TOPIC = "org.nom.api-ressurs-state-v4"
}
}
