package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import io.micrometer.core.annotation.Timed
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt.AnsattIdentifikatorer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomOperasjoner
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class AnsattTjeneste(private val entra: EntraTjeneste, private val ansatte: NomOperasjoner, private val brukere: BrukerTjeneste)  {
    private val log = getLogger(javaClass)
    fun ansatt(ansattId: AnsattId) =
        entra.ansatt(ansattId).let { ansatt ->
            val ansattFnr = ansatte.fnrForAnsatt(ansattId)
            val ansattBruker = ansattFnr?.let { brukere.bruker(it.verdi) }
            Ansatt(AnsattIdentifikatorer(ansattId, ansatt.oid), ansatt.grupper, ansattBruker).also {
                log.trace(CONFIDENTIAL, "Ansatt er {}", it)
            }
        }
}


