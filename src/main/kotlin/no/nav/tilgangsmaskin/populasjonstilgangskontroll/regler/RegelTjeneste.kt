package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringSjekker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val overstyringSjekker: OverstyringSjekker)  {

    private val log = LoggerFactory.getLogger(javaClass)

    fun alleRegler(ansattId: AnsattId, brukerId: BrukerId) =
        runCatching {
            motor.alleRegler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))
        }.getOrElse {
            overstyringSjekker.sjekk(ansattId, brukerId, it)
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))

    fun bulkRegler(ansattId: AnsattId, vararg specs: RegelSpec) {
        log.info("Sjekker regler for ${ansattId.verdi} og ${specs.size} brukere ${specs.map { it.brukerId.verdi }}")
        val ansatt = ansattTjeneste.ansatt(ansattId)
        val avvisninger = mutableListOf<RegelException>()
        specs.forEachIndexed { index, spec ->
            runCatching {
               log.info("[$index] Sjekker ${spec.type.tekst} for '${ansattId.verdi}' og '${spec.brukerId.verdi}'")
                motor.sjekk(ansatt, brukerTjeneste.bruker(spec.brukerId), spec.type)
            }.getOrElse {e -> if (e is RegelException) avvisninger.add(e) else throw e }
        }
        if (avvisninger.isNotEmpty()) throw BulkRegelException(avvisninger).also {
            log.info("Det er ${avvisninger.size} avvisning(er) i regler for ${ansattId.verdi}->${avvisninger.map { it.brukerId.verdi to it.body.title }}")
        }
    }
}


