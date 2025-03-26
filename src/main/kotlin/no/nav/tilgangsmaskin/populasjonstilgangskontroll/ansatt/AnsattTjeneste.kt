package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import io.micrometer.core.annotation.Timed
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomOperasjoner
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt.AnsattIdentifikatorer

@Service
@Timed
class AnsattTjeneste(private val entra: EntraTjeneste, private val nom: NomOperasjoner, private val pdl: BrukerTjeneste)
{
    private val log = getLogger(AnsattTjeneste::class.java)
    fun ansatt(ansattId: AnsattId)  =
        runBlocking {
            val entra =  async { entra.ansatt(ansattId) }.await()
            val ansattFnr =  async { nom.fnrForAnsatt(ansattId) }.await()
            val ansattBruker = ansattFnr?.let { pdl.bruker(it) }
            Ansatt(AnsattIdentifikatorer(ansattId, entra.oid, ansattFnr), entra.grupper, ansattBruker).also {
                log.trace(CONFIDENTIAL,"Ansatt er {}", it)
            }
        }
}
