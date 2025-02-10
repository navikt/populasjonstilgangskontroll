package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.util.UUID

@ConfigurationProperties(GRAPH)
class EntraConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) :
    AbstractRestConfig(baseUri, pingPath, GRAPH, enabled) {

    fun userURI(navIdent: String) = builder().path(USERS_PATH)
        .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_USER)
        .queryParam(PARAM_NAME_FILTER, "onPremisesSamAccountName eq '$navIdent'")
        .queryParam(PARAM_NAME_COUNT, "true")
        .build()

    fun grupperURI(ansattId: String) = builder().path(GRUPPER_PATH)
        .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_GROUPS)
        .queryParam(PARAM_NAME_COUNT, "true")
        .queryParam(PARAM_NAME_TOP, "50")
        .build(ansattId)

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val HEADER_CONSISTENCY_LEVEL = "ConsistencyLevel" to "eventual"
        const val GRAPH = "graph"
        private const val USERS_PATH: String = "/users"
        private const val GRUPPER_PATH = "/users/{ansattId}/memberOf"
        private const val PARAM_NAME_SELECT: String = "\$select"
        private const val PARAM_NAME_FILTER: String = "\$filter"
        private const val PARAM_NAME_COUNT: String = "\$count"
        private const val PARAM_VALUE_SELECT_USER: String =
            "id,onPremisesSamAccountName,displayName,givenName,surname,streetAddress"
        private const val PARAM_VALUE_SELECT_GROUPS: String = "id,displayName"
        private const val DEFAULT_PING_PATH = "/organization"
        private const val PARAM_NAME_TOP = "\$top"
    }
}