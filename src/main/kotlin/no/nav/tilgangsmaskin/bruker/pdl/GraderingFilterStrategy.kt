package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.FORTROLIG
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.STRENGT_FORTROLIG
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.STRENGT_FORTROLIG_UTLAND
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.listener.adapter.RecordFilterStrategy
import org.springframework.stereotype.Component
import kotlin.collections.contains

@Component
class GraderingFilterStrategy : RecordFilterStrategy<String, Personhendelse> {

    override fun filter(hendelse: ConsumerRecord<String, Personhendelse>) =
        hendelse.skalFiltreres()

    private fun ConsumerRecord<String, Personhendelse>.skalFiltreres() = (value().adressebeskyttelse?.gradering !in UFILTRERTE_GRADERINGER).also {
        log(it, value())
    }

    companion object {
        private val log = getLogger(GraderingFilterStrategy::class.java)
        private fun log(resultat: Boolean, hendelse: Personhendelse) =
            if (resultat) {
                log.trace(CONFIDENTIAL, "Filtrerte bort PDL hendelse {}", hendelse)
            } else {
                log.info(CONFIDENTIAL, "Konsumerte PDL hendelse $hendelse")
            }
        private val UFILTRERTE_GRADERINGER = setOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND, FORTROLIG)
    }
}