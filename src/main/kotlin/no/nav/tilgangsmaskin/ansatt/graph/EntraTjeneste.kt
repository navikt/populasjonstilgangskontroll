package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.tilgang.Token.TokenType.*
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverableService
@Cacheable(cacheNames = [GRAPH])
@Timed(value = "entra", histogram = true)
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val oidService: EntraOidService, private val token: Token)  {

    fun geoOgGlobaleGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), true)

    fun geoGrupper(ansattId: AnsattId) = adapter.grupper(resolve(ansattId), false)

    private fun resolve(ansattId: AnsattId) =
        when (token.type) {
            OBO -> token.oid!!
            CC,UNAUTHENTICATED ->  oidService.oidForAnsatt(ansattId)
        }

    override fun toString() = "${javaClass.simpleName} [adapter=$adapter oidService=$oidService]"
}

