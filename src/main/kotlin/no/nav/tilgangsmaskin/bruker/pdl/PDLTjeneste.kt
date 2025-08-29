package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverableService
class PDLTjeneste(private val adapter: PdlRestClientAdapter, private val graphQL: PdlSyncGraphQLClientAdapter) {

    @WithSpan("pdltjeneste.medutvidetfamile")
    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medUtvidetFamile(id: String) = adapter.medUtvidetFamile(id,graphQL.partnere(id))

    @WithSpan("pdltjeneste.mednermestefamilie")
    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medNærmesteFamilie(id: String) = adapter.person(id)

    @WithSpan("pdltjeneste.personer")
    fun personer(ids: Set<String>) =  adapter.personer(ids)

}
