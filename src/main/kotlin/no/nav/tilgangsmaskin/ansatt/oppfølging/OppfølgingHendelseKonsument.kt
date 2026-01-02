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
class OppfølgingHendelseKonsument(private val db: OppfølgingJPAAdapter) {

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING + "xxxx")

    fun listen(hendelse: OppfølgingHendelse) {
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> oppfølgingRegistrert(hendelse)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> kontorEndret(hendelse)
            OPPFOLGING_AVSLUTTET -> oppfølgingAvsluttet(hendelse)
        }
    }

    private fun oppfølgingRegistrert(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            db.startOppfølging(oppfolgingsperiodeUuid,ident, aktorId, startTidspunkt,kontor!!.kontorId)
        }

    private fun kontorEndret(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            db.oppdaterKontor(oppfolgingsperiodeUuid,ident, aktorId, startTidspunkt,kontor!!.kontorId)
        }

    private fun oppfølgingAvsluttet(hendelse: OppfølgingHendelse) =
            db.avsluttOppfølging(hendelse.oppfolgingsperiodeUuid)

}


