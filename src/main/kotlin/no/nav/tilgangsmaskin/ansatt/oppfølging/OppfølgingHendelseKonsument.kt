package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_ERROR_HANDLER
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Avsluttet
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.KontorEndret
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Startet
import no.nav.tilgangsmaskin.bruker.Identer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingTjeneste) {

    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [OPPFØLGING_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING,
        errorHandler = OPPFØLGING_ERROR_HANDLER)
    fun listen(hendelse: OppfølgingHendelse) =
        when (val endring = hendelse.tilDomene()) {
            is Startet -> {
                oppfølging.registrer(endring)
                log.info("Oppfølging startet for kontor {} og id {}", endring.kontor.kontorId.verdi, endring.uuid)
            }
            is KontorEndret -> {
                oppfølging.registrer(endring)
                log.info("Oppfølging endret for kontor {} og id {}", endring.kontor.kontorId.verdi, endring.uuid)
            }
            is Avsluttet -> {
                oppfølging.avslutt(endring)
                log.info("Oppfølging avsluttet for {}", endring.uuid)
            }
        }

    private fun OppfølgingHendelse.tilDomene(): Oppfølgingsendring {
        val identer = Identer(ident, aktorId)
        fun krevKontor() = requireNotNull(kontor) {
            "kontor mangler for $sisteEndringsType (uuid=$oppfolgingsperiodeUuid)"
        }
        return when (sisteEndringsType) {
            OPPFOLGING_STARTET -> Startet(oppfolgingsperiodeUuid, identer, krevKontor(), startTidspunkt)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> KontorEndret(oppfolgingsperiodeUuid, identer, krevKontor(), startTidspunkt)
            OPPFOLGING_AVSLUTTET -> Avsluttet(oppfolgingsperiodeUuid, identer)
        }
    }

    companion object {
        private const val OPPFØLGING_TOPIC = "poao.siste-oppfolgingsperiode-v3"
    }
}


