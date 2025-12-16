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
class OppfølgingHendelseKonsument(private val db: OppfølgingJPAAdapter) {
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
        with(hendelse) {
            log.info("Oppfølging registrert for $oppfolgingsperiodeUuid grunnet hendelse $this")
            db.startOppfølging(oppfolgingsperiodeUuid,ident, aktorId, startTidspunkt,kontor!!.kontorId)
        }
    }
    private fun kontorEndret(hendelse: OppfølgingHendelse) {
        with(hendelse) {
            log.info("Oppfølging kontor endret for $oppfolgingsperiodeUuid grunnet hendelse $this")
            db.oppdaterKontor(oppfolgingsperiodeUuid, kontor!!.kontorId).also {
                if (it > 0) log.info("Oppfølging kontor  endret til ${kontor.kontorId} for $oppfolgingsperiodeUuid")
            }
        }
    }
    private fun oppfølgingAvsluttet(hendelse: OppfølgingHendelse) {
        with(hendelse) {
            log.info("Oppfølging avsluttet for $oppfolgingsperiodeUuid grunnet hendelse $this")
            db.avsluttOppfølging(oppfolgingsperiodeUuid).also {
                if (it > 0) log.info("Oppfølging avsluttet OK for $oppfolgingsperiodeUuid")
            }
        }
    }
}

