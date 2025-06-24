package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@Transactional
@Timed
@RetryingOnRecoverableService
class Nom(private val adapter: NomJPAAdapter) {

    private val log = getLogger(javaClass)

    fun lagre(ansattData: NomAnsattData) = adapter.upsert(ansattData)

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [NOM])
    fun fnrForAnsatt(ansattId: AnsattId) = adapter.fnrForAnsatt(ansattId.verdi)
    fun ryddOpp() = adapter.ryddOpp().also {
        if (it > 0) log.info("Vaktmester ryddet opp $it rad(er) med utgått informasjon om ansatte som ikke lenger jobber i Nav")
    }
}

