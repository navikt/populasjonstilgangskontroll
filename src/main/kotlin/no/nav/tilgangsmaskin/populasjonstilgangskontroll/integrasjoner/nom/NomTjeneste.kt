package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Service

@Service
class NomTjeneste(private val adapter: NomJPAAdapter) {

    fun fnrForAnsatt(ansattId: AnsattId) = BrukerId(adapter.fnrForAnsatt(ansattId.verdi))
}