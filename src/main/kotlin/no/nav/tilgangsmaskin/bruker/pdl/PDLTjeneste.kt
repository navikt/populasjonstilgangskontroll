package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@RetryingWhenRecoverable
@Service
class PDLTjeneste(private val adapter: PdlRestClientAdapter, private val graphQL: PdlSyncGraphQLClientAdapter) {

    @WithSpan
    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medUtvidetFamile(id: String) = adapter.medUtvidetFamile(id,graphQL.partnere(id))

    @WithSpan
    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medFamilie(id: String) = adapter.person(id)

    @CacheEvict(cacheNames = [PDL], key = "#root.methodName.replace('Evict', '') + ':' + #id")
    fun medFamilieEvict(id: String) = Unit

    @WithSpan
    fun personer(ids: Set<String>) =  adapter.personer(ids)

}
