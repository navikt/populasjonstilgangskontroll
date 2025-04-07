package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.IdOgType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
@Timed
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val overstyringTjeneste: OverstyringTjeneste)  {
    private val log = LoggerFactory.getLogger(javaClass)

    fun kompletteRegler(ansattId: AnsattId, brukerId: BrukerId) =
        measureTime {
            with(brukerTjeneste.bruker(brukerId)) {
                runCatching {
                    motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), this)
                }.getOrElse {
                    overstyringTjeneste.sjekk(ansattId, it)
                }
            }
        }.also {
            log.info("Tid brukt på komplett regelsett for ansatt $ansattId og bruker $brukerId: ${it.inWholeMilliseconds}ms")
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: List<IdOgType>) =
        runCatching {
            motor.bulkRegler(ansattTjeneste.ansatt(ansattId), idOgType.brukerOgType())
        }.getOrElse {
            overstyringTjeneste.sjekk(ansattId, it)
        }

    private fun List<IdOgType>.brukerOgType() =
         mapNotNull { spec ->
            brukerTjeneste.brukere(map {
                it.brukerId
            }).associateBy {
                it.brukerId
            }[spec.brukerId]?.let {
                it to spec.type
            }
        }
}


