package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.ARBEIDSOPPFOLGINGSKONTOR_ENDRET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_AVSLUTTET
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.EndringType.OPPFOLGING_STARTET
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.javaClass
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.OPPFØLGING

@Component
@Transactional
class OppfølgingHendelseKonsument(private val oppfølging: OppfølgingJPAAdapter) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = ["poao.siste-oppfolgingsperiode-v2"],
        properties = ["spring.json.value.default.type=no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse"],
        groupId = "$OPPFØLGING-hendelse5")

    fun listen(hendelse: OppfølgingHendelse) {
        when (hendelse.sisteEndringsType) {
            OPPFOLGING_STARTET -> start(hendelse)
            ARBEIDSOPPFOLGINGSKONTOR_ENDRET -> endre(hendelse)
            OPPFOLGING_AVSLUTTET -> avslutt(hendelse)
        }
    }

    private fun start(hendelse: OppfølgingHendelse) {
        log.info("Starter oppfølging ${hendelse.oppfolgingsperiodeUuid} $hendelse")
        oppfølging.start(hendelse)
    }
    private fun endre(hendelse: OppfølgingHendelse) {
        log.info("Endrer oppfølging ${hendelse.oppfolgingsperiodeUuid} $hendelse")
        oppfølging.oppdater(hendelse.oppfolgingsperiodeUuid, hendelse.kontor!!.kontorId.verdi)
    }
    private fun avslutt(hendelse: OppfølgingHendelse) {
        log.info("Sletter oppfølging ${hendelse.oppfolgingsperiodeUuid} $hendelse")
        oppfølging.slett(hendelse.oppfolgingsperiodeUuid)
    }
}

