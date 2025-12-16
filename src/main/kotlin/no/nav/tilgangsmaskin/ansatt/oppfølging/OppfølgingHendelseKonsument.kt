package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import kotlin.jvm.javaClass

@Component
class OppfølgingHendelseKonsument(private val `oppfølging`: OppfølgingTjeneste) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = "$OPPFØLGING-hendelse1")
    fun listen(hendelse: OppfølgingHendelse) {
        log.info("Mottok oppfølginghendelse ${hendelse.sisteEndringsType}: $hendelse")
        when (val type = hendelse.sisteEndringsType) {
            OPPFOLGING_AVSLUTTET ->  {
                log.info("Sletter oppfølging ${hendelse.oppfolgingsperiodeUuid}")
                `oppfølging`.slett(hendelse.oppfolgingsperiodeUuid)
            }
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET,
            OPPFOLGING_STARTET -> log.info("Ignorerer foreløpig lagring av oppfølginghendelse av type $type")
        }
    }
}

