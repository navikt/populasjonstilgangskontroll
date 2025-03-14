package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import kotlinx.coroutines.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AnsattTjeneste(private val entra: EntraTjeneste, private val nom: NomTjeneste, private val pdl: BrukerTjeneste) {
    private val log = LoggerFactory.getLogger(AnsattTjeneste::class.java)


    fun ansatt(ansattId: AnsattId)  =
        runBlocking {
            val entra =  async { entra.ansatt(ansattId) }.await()
            val fnr =  async { nom.fnrForAnsatt(ansattId) }.await()
            val  ansattBruker = fnr?.let { pdl.bruker(it) }
            Ansatt(ansattBruker, AnsattIdentifikatorer(ansattId,entra.oid,fnr),entra.grupper).also {
                log.trace("Ansatt er {}", it)
            }
        }
}