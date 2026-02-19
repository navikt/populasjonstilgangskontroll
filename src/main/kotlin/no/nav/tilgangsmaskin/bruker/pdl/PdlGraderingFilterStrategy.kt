package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.FORTROLIG
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.STRENGT_FORTROLIG
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlClientBeanConfig.Companion.PDL_GRADERING_FILTER
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.listener.adapter.RecordFilterStrategy
import org.springframework.stereotype.Component

@Component(PDL_GRADERING_FILTER)
class PdlGraderingFilterStrategy : RecordFilterStrategy<String, Personhendelse> {

    private val log = getLogger(javaClass)

    override fun filter(hendelse: ConsumerRecord<String, Personhendelse>) =
        hendelse.skalFiltreres()

    private fun ConsumerRecord<String, Personhendelse>.skalFiltreres() = (value().adressebeskyttelse?.gradering !in UFILTRERTE_GRADERINGER).also {
        log(it, value())
    }
    private fun log(resultat: Boolean, hendelse: Personhendelse) =
        if (resultat) {
            log.trace(CONFIDENTIAL, "Filtrerte bort PDL hendelse {}", hendelse)
        } else {
            log.info(CONFIDENTIAL, "Konsumerte PDL hendelse $hendelse")
        }

    companion object {
        private val UFILTRERTE_GRADERINGER = setOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND, FORTROLIG)
    }
}