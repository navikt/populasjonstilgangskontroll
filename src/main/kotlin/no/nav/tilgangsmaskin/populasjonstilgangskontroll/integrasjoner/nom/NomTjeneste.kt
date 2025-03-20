package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import io.micrometer.core.annotation.Timed
import no.nav.boot.conditionals.ConditionalOnDev
import no.nav.boot.conditionals.ConditionalOnNotDev
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
@Timed
@ConditionalOnNotDev
class NomTjeneste(private val adapter: NomJPAAdapter) : NomOperasjoner {

    override fun lagre(ansattId: AnsattId, fnr: BrukerId, startdato: LocalDate?, sluttdato: LocalDate?) = adapter.upsert(ansattId.verdi, fnr.verdi, startdato, sluttdato)

    @Transactional(readOnly = true)
    override fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)

    override fun ryddOpp() = adapter.ryddOpp()
}
/**
 * NOM har en rekke fødsesnummer i dev som ikke finnes i PDL
 */
@ConditionalOnDev
class NomDevTjeneste(adapter: NomJPAAdapter): NomTjeneste(adapter) {
    override fun fnrForAnsatt(ansattId: AnsattId) = null
}

interface NomOperasjoner {

    fun fnrForAnsatt(ansattId: AnsattId): BrukerId?
    fun ryddOpp(): Int
    fun lagre(ansattId: AnsattId, fnr: BrukerId, startdato: LocalDate? = null, sluttdato: LocalDate? = null): Long
}
