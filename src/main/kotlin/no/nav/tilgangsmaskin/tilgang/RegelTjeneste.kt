package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
@Timed
class RegelTjeneste(
        private val motor: RegelMotor,
        private val brukere: BrukerTjeneste,
        private val ansatte: AnsattTjeneste,
        private val overstyring: OverstyringTjeneste) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun kompletteRegler(ansattId: AnsattId, brukerId: String) =
        measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            with(brukere.utvidetFamilie(brukerId)) {
                runCatching {
                    motor.kompletteRegler(ansatte.ansatt(ansattId), this)
                }.getOrElse {
                    overstyring.sjekk(ansattId, it)
                }
            }
        }.also {
            log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${it.inWholeMilliseconds}ms")
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.utvidetFamilie(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: List<IdOgType>) =
        runCatching {
            motor.bulkRegler(ansatte.ansatt(ansattId), idOgType.brukerIdOgType())
        }.getOrElse {
            overstyring.sjekk(ansattId, it)
        }

    private fun List<IdOgType>.brukerIdOgType(): List<Pair<Bruker, RegelType>> =
        mapNotNull {
            brukere.brukere(map { it.brukerId }.toSet())
                .associateBy { it.brukerId.verdi }[it.brukerId]?.let { bruker ->
                bruker to it.type
            }
        }
}


