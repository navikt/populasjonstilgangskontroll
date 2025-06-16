package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Timed
@CacheableRetryingOnRecoverableService(cacheNames = [NOM])
class Nom(private val adapter: NomJPAAdapter) {

    fun lagre(ansattData: NomAnsattData) = adapter.upsert(ansattData)

    @Transactional(readOnly = true)
    fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)
    fun ryddOpp() = adapter.ryddOpp()
}

