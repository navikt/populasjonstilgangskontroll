package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverableService
class PDLTjeneste(private val adapter: PdlRestClientAdapter, private val graphQL: PdlSyncGraphQLClientAdapter) {

    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medUtvidetFamile(id: String) = adapter.medUtvidetFamile(id,graphQL.partnere(id))

    @Cacheable(cacheNames = [PDL],  key = "#root.methodName + ':' + #id")
    fun medNærmesteFamilie(id: String) = adapter.person(id)

    fun personer(ids: Set<String>) =  adapter.personer(ids)

}
