package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.javaClass

@Component
@Transactional
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingJPAAdapter) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING)

    fun listen(hendelse: OppfølgingHendelse) {
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> oppfølgingRegistrert(hendelse)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> kontorEndret(hendelse)
            OPPFOLGING_AVSLUTTET -> oppfølgingAvsluttet(hendelse)
        }
    }

    private fun oppfølgingRegistrert(hendelse: OppfølgingHendelse) {
        log.info("Oppfølging registrert ${hendelse.oppfolgingsperiodeUuid} $hendelse")
        oppfølging.startOppfølging(hendelse)
    }
    private fun kontorEndret(hendelse: OppfølgingHendelse) {
        log.info("Oppfølging kontor endret ${hendelse.oppfolgingsperiodeUuid} $hendelse")
        oppfølging.oppdaterKontor(hendelse.oppfolgingsperiodeUuid, hendelse.kontor!!.kontorId.verdi).also {
            if (it > 0) log.info("Oppfølging kontor  endret til ${hendelse.kontor.kontorId} for ${hendelse.oppfolgingsperiodeUuid}")
        }
    }
    private fun oppfølgingAvsluttet(hendelse: OppfølgingHendelse) {
        log.info("Oppfølging avsluttet ${hendelse.oppfolgingsperiodeUuid} $hendelse")
        oppfølging.avsluttOppfølging(hendelse.oppfolgingsperiodeUuid).also {
            if (it > 0) log.info("Oppfølging avsluttet OK for ${hendelse.oppfolgingsperiodeUuid}")
        }
    }
}

