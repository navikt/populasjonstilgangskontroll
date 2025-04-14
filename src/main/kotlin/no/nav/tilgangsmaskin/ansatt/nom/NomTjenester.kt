package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.transaction.annotation.Transactional

//@Service
//@Transactional
//@Timed
//@ConditionalOnNotDev
open class NomTjeneste(private val adapter: NomJPAAdapter) : NomOperasjoner {

    override fun lagre(ansattData: NomAnsattData) = adapter.upsert(ansattData)

    @Transactional(readOnly = true)
    override fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)

    override fun ryddOpp() = adapter.ryddOpp()
}

/**
 * NOM har en rekke fødsesnummer i dev som ikke finnes i PDL
 */
@ConditionalOnGCP
class NomDevTjeneste(adapter: NomJPAAdapter) : NomTjeneste(adapter) {
    override fun fnrForAnsatt(ansattId: AnsattId) = null
}

interface NomOperasjoner {

    fun fnrForAnsatt(ansattId: AnsattId): BrukerId?
    fun ryddOpp(): Int
    fun lagre(ansattData: NomAnsattData): Long
}
