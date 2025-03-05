package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomTjeneste
import org.springframework.stereotype.Service

@Service
class AnsattTjeneste(private val entra: EntraTjeneste, private val nom: NomTjeneste) {


    fun ansatt(ansattId: AnsattId) : Ansatt {
        val entraData = entra.ansatt(ansattId)
        val fnr = nom.fnrForAnsatt(ansattId)
        return Ansatt(fnr, entraData.attributter,*entraData.grupper.toTypedArray())
    }
}