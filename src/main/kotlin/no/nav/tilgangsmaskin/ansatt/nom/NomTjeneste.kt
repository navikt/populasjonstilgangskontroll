package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Timed
class NomTjeneste(private val adapter: NomJPAAdapter) {

    fun lagre(ansattData: NomAnsattData) = adapter.upsert(ansattData)

    @Transactional(readOnly = true)
    fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)
    fun ryddOpp() = adapter.ryddOpp()
}

