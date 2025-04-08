package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.IdOgType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KOMPLETT_REGELTYPE
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
@Timed
class RegelTjeneste(private val motor: RegelMotor, private val brukere: BrukerTjeneste, private val ansatte: AnsattTjeneste, private val overstyring: OverstyringTjeneste)  {
    private val log = LoggerFactory.getLogger(javaClass)

    fun kompletteRegler(ansattId: AnsattId, brukerId: BrukerId) =
        measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og $brukerId")
            with(brukere.bruker(brukerId)) {
                runCatching {
                    motor.kompletteRegler(ansatte.ansatt(ansattId), this)
                }.getOrElse {
                    overstyring.sjekk(ansattId, it)
                }
            }
        }.also {
            log.info("Tid brukt på komplett regelsett for ansatt $ansattId og $brukerId: ${it.inWholeMilliseconds}ms")
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.bruker(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: List<IdOgType>) =
        runCatching {
            motor.bulkRegler(ansatte.ansatt(ansattId), idOgType.brukerOgType())
        }.getOrElse {
            overstyring.sjekk(ansattId, it)
        }

    private fun List<IdOgType>.brukerOgType() =
         mapNotNull { spec ->
            brukere.brukere(map {
                it.brukerId
            }).associateBy {
                it.brukerId
            }[spec.brukerId]?.let {
                it to spec.type
            }
        }
}


