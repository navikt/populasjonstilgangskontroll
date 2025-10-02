package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverable
import org.springframework.stereotype.Service
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverable
@Service
class PDLTjeneste(private val adapter: PdlRestClientAdapter, private val graphQL: PdlSyncGraphQLClientAdapter) {

    @WithSpan
    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medUtvidetFamile(id: String) = adapter.medUtvidetFamile(id,graphQL.partnere(id))

    @WithSpan
    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medFamilie(id: String) = adapter.person(id)

    @WithSpan
    fun personer(ids: Set<String>) =  adapter.personer(ids)

}
