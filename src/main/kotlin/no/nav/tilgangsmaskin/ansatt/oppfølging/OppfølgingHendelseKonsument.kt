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
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING)

    fun listen(hendelse: OppfølgingHendelse) {
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> registrer(hendelse).also {
                log.info("Oppfølging registrert for ${hendelse.oppfolgingsperiodeUuid}")
            }
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> registrer(hendelse).also {
                log.info("Oppfølgingskontor endret for ${hendelse.oppfolgingsperiodeUuid}")
            }
            OPPFOLGING_AVSLUTTET -> avslutt(hendelse)
        }
    }

    private fun registrer(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.registrer(oppfolgingsperiodeUuid,
                Identer(ident, aktorId), kontor!!, startTidspunkt).also {
                log.info("Oppfølging kontor endret til ${kontor.kontorId.verdi} for $oppfolgingsperiodeUuid")
            }
        }

    private fun avslutt(hendelse: OppfølgingHendelse) =
        with(hendelse) {
            oppfølging.avslutt(oppfolgingsperiodeUuid, Identer(ident, aktorId))
        }
}


