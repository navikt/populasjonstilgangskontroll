package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.*
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.Identer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingTjeneste) {

    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [OPPFØLGING_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING)

    fun listen(hendelse: OppfølgingHendelse) =
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> registrer(hendelse, "Oppfølging startet")
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> registrer(hendelse, "Oppfølging endret")
            OPPFOLGING_AVSLUTTET -> avslutt(hendelse,"Oppfølging avsluttet")
        }

    private fun registrer(hendelse: OppfølgingHendelse, melding: String) =
        with(hendelse) {
            oppfølging.registrer(oppfolgingsperiodeUuid,
                Identer(ident, aktorId), kontor!!, startTidspunkt)
            log.info("$melding til ${kontor.kontorId.verdi} for $oppfolgingsperiodeUuid")
        }

    private fun avslutt(hendelse: OppfølgingHendelse, melding: String) =
        with(hendelse) {
            oppfølging.avslutt(oppfolgingsperiodeUuid, Identer(ident, aktorId))
            log.info("$melding for $oppfolgingsperiodeUuid")
        }

    companion object {
        private const val OPPFØLGING_TOPIC  = "poao.siste-oppfolgingsperiode-v3"
    }
}


