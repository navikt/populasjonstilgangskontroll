package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingEndring.Avsluttet
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingEndring.StartetEllerEndret
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingTjeneste) {

    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [OPPFØLGING_TOPIC],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = OPPFØLGING)
    fun listen(hendelse: OppfølgingHendelse) =
        when (val endring = hendelse.tilDomene()) {
            is StartetEllerEndret -> {
                if (endring.type == ARBEIDSOPPFOLGINGSKONTOR_ENDRET) {
                    val tidligereKontor = oppfølging.enhetFor(Identifikator(endring.identer.brukerId.verdi))
                    if (tidligereKontor == null) {
                        log.warn("Mottok KontorEndret for id {} uten eksisterende kontor — ingen tidligere oppfølging funnet", endring.uuid)
                    }
                    oppfølging.registrer(endring).also {
                        log.info("Oppfølging endret fra kontor {} til kontor {} for id {}", tidligereKontor?.verdi, endring.kontor.kontorId.verdi, endring.uuid)
                    }
                } else {
                    oppfølging.registrer(endring).also {
                        log.info("Oppfølging startet med kontor {} og id {}", endring.kontor.kontorId.verdi, endring.uuid)
                    }
                }
            }
            is Avsluttet ->
                oppfølging.avslutt(endring).also {
                    log.info("Oppfølging avsluttet for id {}", endring.uuid)
            }
        }

    companion object {
        private const val OPPFØLGING_TOPIC = "poao.siste-oppfolgingsperiode-v3"
    }
}

fun OppfølgingHendelse.tilDomene(): OppfølgingEndring {
    val identer = Identer(ident, aktorId)
    fun krevKontor() = requireNotNull(kontor) {
        "kontor mangler for $sisteEndringsType (uuid=$oppfolgingsperiodeUuid)"
    }
    return when (sisteEndringsType) {
        OPPFOLGING_STARTET, ARBEIDSOPPFOLGINGSKONTOR_ENDRET ->
            StartetEllerEndret(oppfolgingsperiodeUuid, identer, krevKontor(), startTidspunkt, sisteEndringsType)
        OPPFOLGING_AVSLUTTET -> Avsluttet(oppfolgingsperiodeUuid, identer)
    }
}



