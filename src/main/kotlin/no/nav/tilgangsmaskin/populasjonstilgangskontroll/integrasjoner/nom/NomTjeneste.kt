package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
@Timed
class NomTjeneste(private val adapter: NomJPAAdapter) {

    fun lagre(ansattId: AnsattId, fnr: BrukerId, sluttdato: LocalDate? = null) = adapter.upsert(ansattId.verdi, fnr.verdi, sluttdato)

    @Transactional(readOnly = true)
    fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)

    fun ryddOpp() = adapter.ryddOpp()
}