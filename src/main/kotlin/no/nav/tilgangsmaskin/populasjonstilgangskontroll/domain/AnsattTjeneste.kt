package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import io.micrometer.core.annotation.Timed
import kotlinx.coroutines.*
import no.nav.boot.conditionals.Cluster.Companion.isDev
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Timed
class AnsattTjeneste(private val entra: EntraTjeneste, private val nom: NomTjeneste, private val pdl: BrukerTjeneste) {
    private val log = LoggerFactory.getLogger(AnsattTjeneste::class.java)

    fun ansatt(ansattId: AnsattId)  =
        runBlocking {
            val entra =  async { entra.ansatt(ansattId) }.await()
            val ansattFnr =  async { nom.fnrForAnsatt(ansattId) }.await()
            Ansatt(ansattBruker(ansattId), AnsattIdentifikatorer(ansattId, entra.oid, ansattFnr), entra.grupper).also {
                log.trace("Ansatt er {}", it)
            }
        }

    private fun ansattBruker(ansattId: AnsattId) =
        if (!isDev()) {
            nom.fnrForAnsatt(ansattId)?.let { pdl.bruker(it) }
        } else null
}