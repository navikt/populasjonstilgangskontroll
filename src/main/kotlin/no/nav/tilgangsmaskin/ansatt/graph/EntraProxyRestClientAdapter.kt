package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.graph.EntraProxyConfig.Companion.PROXY
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@ConditionalOnNotProd
class EntraProxyRestClientAdapter(@Qualifier(PROXY) restClient: RestClient, val cf: EntraProxyConfig) :
    AbstractRestClientAdapter(restClient, cf) {

    fun tema(tema: String) =
        get<Set<String>>(cf.temaURI(tema))

    fun enhet(enhet: String) =
        get<Set<String>>(cf.enhetURI(enhet))

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

}

