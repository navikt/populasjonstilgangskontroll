package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringSjekker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.*
import org.springframework.stereotype.Service

@Service
@Timed
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val sjekker: OverstyringSjekker)  {

    fun kompletteRegler(ansattId: AnsattId, brukerId: BrukerId) =
        with(brukerTjeneste.bruker(brukerId)) {
            runCatching {
                motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), this)
            }.getOrElse {
                sjekker.sjekk(ansattId, it)
            }
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: List<IdOgType>) =
        runCatching {
            motor.bulkRegler(ansattTjeneste.ansatt(ansattId), idOgType.brukerOgType())
        }.getOrElse {
            sjekker.sjekk(ansattId, it)
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


