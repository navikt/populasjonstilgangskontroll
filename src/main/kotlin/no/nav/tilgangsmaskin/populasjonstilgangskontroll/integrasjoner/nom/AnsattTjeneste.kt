package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Service

@Service
class AnsattTjeneste(private val adapter: AnsattJPAAdapter) {

    fun FnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)?.let { BrukerId(it) }
}