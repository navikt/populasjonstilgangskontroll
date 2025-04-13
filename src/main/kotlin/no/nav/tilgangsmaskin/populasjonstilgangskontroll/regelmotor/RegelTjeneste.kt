package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.IdOgType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
@Timed
class RegelTjeneste(private val motor: RegelMotor, private val brukere: BrukerTjeneste, private val ansatte: AnsattTjeneste, private val overstyring: OverstyringTjeneste)  {
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
            log.info("Tid brukt på komplett regelsett for ansatt $ansattId og ${brukerId.maskFnr()}: ${it.inWholeMilliseconds}ms")
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.utvidetFamilie(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: List<IdOgType>) =
        runCatching {
            motor.bulkRegler(ansatte.ansatt(ansattId), idOgType.brukerIdOgType())
        }.getOrElse {
            overstyring.sjekk(ansattId, it)
        }

    private fun List<IdOgType>.brukerIdOgType(): List<Pair<Bruker, RegelSett.RegelType>> =
         mapNotNull {
            brukere.brukere(map { it.brukerId }.toSet())
                .associateBy { it.brukerId.verdi }[it.brukerId]?.let { bruker ->
                bruker to it.type
            }
    }
}


