package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@Timed
@RetryingWhenRecoverableRestService
@Transactional
class NomTjeneste(private val adapter: NomJPAAdapter) {

    private val log = getLogger(javaClass)

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [NOM],  key = "#ansattId.verdi")
    @WithSpan
    fun fnrForAnsatt(ansattId: AnsattId) =
        adapter.fnrForAnsatt(ansattId.verdi)

    fun ryddOpp() =
        adapter.ryddOpp().also {
        if (it > 0) log.info("Fjernet informasjon fra DB om $it ansatte som ikke lenger er ansatt i Nav")
    }

    @CacheEvict(cacheNames = [NOM], key = "#nomAnsattData.ansattId.verdi")
    @WithSpan
    fun lagre(nomAnsattData: NomAnsattData) =
        adapter.upsert(nomAnsattData)
}

