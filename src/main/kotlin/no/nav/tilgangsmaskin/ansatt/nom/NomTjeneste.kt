package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@RetryingWhenRecoverableRestService
@Transactional
class NomTjeneste(private val adapter: NomJPAAdapter) {


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [NOM], key = "#ansattId.verdi")
    fun fnrForAnsatt(ansattId: AnsattId) =
        adapter.fnrForAnsatt(ansattId.verdi)

    fun ryddOpp() =
        adapter.ryddOpp()

    @CacheEvict(cacheNames = [NOM], key = "#nomAnsattData.ansattId.verdi")
    fun lagre(nomAnsattData: NomAnsattData) =
        adapter.upsert(nomAnsattData)
}

