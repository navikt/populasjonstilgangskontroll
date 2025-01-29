package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.util.UUID

@ConfigurationProperties(GRAPH)
class MSGraphConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, GRAPH, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    fun azureUriUser(b: UriBuilder, navIdent: String) =
        b.path(USERS_PATH)
            .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_USER)
            .queryParam(PARAM_NAME_FILTER, "onPremisesSamAccountName eq '$navIdent'")
            .queryParam(PARAM_NAME_COUNT, "true")
            .build()

    fun grupperURI(b: UriBuilder, ansattId: UUID) =
        b.path( USERS_PATH)
            .build("$ansattId")


    /**
    fun azureUriGroups(azureID: UUID, b: UriBuilder) =
        b.path(MEMBER_OF_PATH)
        .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_GROUPS)
        .queryParam(PARAM_NAME_COUNT, "true")
        .build()
    **/

    companion object {
        val HEADER_CONSISTENCY_LEVEL = "ConsistencyLevel" to "eventual"
        const val GRAPH = "graph"
        private const val USERS_PATH: String = "/users"
        private const val GRUPPER_PATH = "$USERS_PATH/{id}/getMemberGroups"
        private const val ME_PATH: String = "/me"
        private const val MEMBER_OF_PATH: String = "/memberOf"
        private const val PARAM_NAME_SELECT: String = "\$select"
        private const val PARAM_NAME_FILTER: String = "\$filter"
        private const val PARAM_NAME_COUNT: String = "\$count"
        private const val PARAM_VALUE_SELECT_USER: String = "id,onPremisesSamAccountName,displayName,givenName,surname,streetAddress"
        private const val PARAM_VALUE_SELECT_GROUPS: String = "id"
        private const val DEFAULT_PING_PATH = ""
    }
}