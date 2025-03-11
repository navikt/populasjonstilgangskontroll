package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AnsattTjeneste(private val entra: EntraTjeneste, private val nom: NomTjeneste, private val pdl: BrukerTjeneste) {
    private val log = LoggerFactory.getLogger(AnsattTjeneste::class.java)


    fun ansatt(ansattId: AnsattId) : Ansatt {
        val entraData = entra.ansatt(ansattId)
        val fnr = nom.fnrForAnsatt(ansattId)
        val  ansattBruker = fnr?.let { pdl.bruker(it) }
        return Ansatt(ansattBruker, ansattId,entraData.oid,*entraData.grupper.toTypedArray()).also {
            log.info("Ansatt er $it")
        }
    }
}