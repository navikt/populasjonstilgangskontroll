package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Observed
@Service
class NomTjeneste(private val adapter: NomJPAAdapter, private val graph: NomSyncGraphQLClientAdapter) {


    fun lederForAnsatt(ansattId: AnsattId) =
        graph.leder(ansattId.verdi)


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [NOM], key = "#ansattId.verdi")
    fun fnrForAnsatt(ansattId: AnsattId) =
        adapter.fnrForAnsatt(ansattId.verdi)

    @Transactional
    fun ryddOpp() =
        adapter.ryddOpp()

    @Transactional
    @CacheEvict(cacheNames = [NOM], key = "#nomAnsattData.ansattId.verdi")
    fun lagre(nomAnsattData: NomAnsattData) =
        adapter.upsert(nomAnsattData)
}
