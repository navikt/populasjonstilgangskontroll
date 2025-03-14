package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NomTjeneste(private val adapter: NomJPAAdapter) {

    fun upsert(ansattId: AnsattId, fnr: BrukerId) = adapter.upsert(ansattId.verdi, fnr.verdi)

    @Transactional(readOnly = true)
    fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)?.let { BrukerId(it) }
}