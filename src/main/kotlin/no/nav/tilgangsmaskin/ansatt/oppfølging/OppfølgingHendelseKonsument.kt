package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.*
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_ERROR_HANDLER
import no.nav.tilgangsmaskin.bruker.Identer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingTjeneste) {


    @KafkaListener(
        topics = [OPPFØLGING_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING +"-debug4",
        errorHandler = OPPFØLGING_ERROR_HANDLER)

    fun listen(hendelse: OppfølgingHendelse) =
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> opprett(hendelse)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> oppdater(hendelse)
            OPPFOLGING_AVSLUTTET -> avslutt(hendelse)
        }

    private fun opprett(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            log("Oppfølging starter for",oppfolgingsperiodeUuid,kontor.kontorId.verdi)
            oppfølging.opprett(oppfolgingsperiodeUuid, Identer(ident, aktorId), kontor!!, startTidspunkt)
            log("Oppfølging startet for",oppfolgingsperiodeUuid,kontor.kontorId.verdi)
        }
    private fun oppdater(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            log("Oppfølging kontor endres til",oppfolgingsperiodeUuid,kontor.kontorId.verdi)
            oppfølging.oppdater(oppfolgingsperiodeUuid, kontor!!, startTidspunkt)
            log("Oppfølging kontor endret til",oppfolgingsperiodeUuid,kontor.kontorId.verdi)
        }

    private fun avslutt(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            log("Oppfølging avsluttes for",oppfolgingsperiodeUuid)
            oppfølging.avslutt(oppfolgingsperiodeUuid, Identer(ident, aktorId))
            log("Oppfølging avsluttet for",oppfolgingsperiodeUuid)
        }

    companion object {
        private val log = getLogger(OppfølgingHendelseKonsument::class.java)
        private fun log(melding: String, id: UUID, kontorId: String? = null) =
            log.info("$melding ${kontorId?.let { "$it " } ?: ""}for $id")
        private const val OPPFØLGING_TOPIC  = "poao.siste-oppfolgingsperiode-v3"
    }
}


