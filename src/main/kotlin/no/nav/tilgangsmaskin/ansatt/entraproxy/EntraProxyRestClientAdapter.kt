package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupper
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.*

@Component
class EntraProxyRestClientAdapter(@Qualifier(ENTRAPROXY) restClient: RestClient, val cf: EntraProxyConfig) :
    AbstractRestClientAdapter(restClient, cf) {

    fun enhetForAnsatt(ansattId: String) =
        get<Enhet>(cf.brukerURI(ansattId))

    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cf, errorHandler=$errorHandler]"

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Enhet(val enhetnummer: Enhetsnummer, val navn: String = "Ukjent")

}