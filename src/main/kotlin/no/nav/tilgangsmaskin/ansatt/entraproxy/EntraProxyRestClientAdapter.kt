package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class EntraProxyRestClientAdapter(@Qualifier(ENTRAPROXY) restClient: RestClient, val cf: EntraProxyConfig) :
    AbstractRestClientAdapter(restClient, cf) {

    fun enhetForAnsatt(ansattId: String) =
        get<ProxiedAnsatt>(cf.brukerURI(ansattId)).enhet

}