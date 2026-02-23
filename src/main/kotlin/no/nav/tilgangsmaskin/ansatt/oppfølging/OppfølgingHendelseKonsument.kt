package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.*
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_ERROR_HANDLER
import no.nav.tilgangsmaskin.bruker.Identer
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingTjeneste) {


    @KafkaListener(
        topics = [OPPFØLGING_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING +"-debug5",
        errorHandler = OPPFØLGING_ERROR_HANDLER)

    fun listen(hendelse: OppfølgingHendelse) =
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> opprett(hendelse)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> oppdater(hendelse)
            OPPFOLGING_AVSLUTTET -> avslutt(hendelse)
        }

    private fun opprett(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.opprett(oppfolgingsperiodeUuid, Identer(ident, aktorId), kontor!!, startTidspunkt)
        }
    private fun oppdater(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.oppdater(oppfolgingsperiodeUuid, kontor!!, startTidspunkt)
                ?: oppfølging.opprett(oppfolgingsperiodeUuid, Identer(ident, aktorId), kontor, startTidspunkt)
        }

    private fun avslutt(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.avslutt(oppfolgingsperiodeUuid, Identer(ident, aktorId))
        }

    companion object {
        private const val OPPFØLGING_TOPIC  = "poao.siste-oppfolgingsperiode-v3"
    }
}


