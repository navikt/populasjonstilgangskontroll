package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingTjeneste) {

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING + "42")

    fun listen(hendelse: OppfølgingHendelse) {
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> oppfølgingRegistrert(hendelse)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> kontorEndret(hendelse)
            OPPFOLGING_AVSLUTTET -> oppfølgingAvsluttet(hendelse)
        }
    }

    private fun oppfølgingRegistrert(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.start(oppfolgingsperiodeUuid,ident,aktorId,startTidspunkt,kontor!!)
        }

    private fun kontorEndret(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.kontorfor(oppfolgingsperiodeUuid,ident,aktorId,startTidspunkt,kontor!!)
        }

    private fun oppfølgingAvsluttet(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.avslutt(oppfolgingsperiodeUuid, ident, aktorId)
        }
}


