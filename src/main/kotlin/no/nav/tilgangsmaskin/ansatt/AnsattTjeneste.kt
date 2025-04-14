package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.Ansatt.AnsattIdentifikatorer
import no.nav.tilgangsmaskin.ansatt.entra.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomOperasjoner
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class AnsattTjeneste(
    private val entra: EntraTjeneste,
    private val ansatte: NomOperasjoner,
    private val brukere: BrukerTjeneste
) {
    private val log = getLogger(javaClass)
    fun ansatt(ansattId: AnsattId) =
        entra.ansatt(ansattId).let { ansatt ->
            val ansattFnr = ansatte.fnrForAnsatt(ansattId)
            val ansattBruker = ansattFnr?.let { brukere.utvidetFamilie(it.verdi) }
            Ansatt(AnsattIdentifikatorer(ansattId, ansatt.oid), ansatt.grupper, ansattBruker).also {
                log.trace(CONFIDENTIAL, "Ansatt er {}", it)
            }
        }
}


