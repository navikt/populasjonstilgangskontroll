package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.util.UriBuilder
import java.net.URI

@ConfigurationProperties(GRAPH)
class MSGraphConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, GRAPH, enabled) {

    protected
    val USERS_PATH: String = "/users"
    protected
    val ME_PATH: String = "/me"
    protected
    val MEMBER_OF_PATH: String = "/memberOf"
    protected
    val PARAM_NAME_SELECT: String = "\$select"
    protected
    val PARAM_NAME_FILTER: String = "\$filter"
    protected
    val PARAM_NAME_COUNT: String = "\$count"
    protected
    val PARAM_VALUE_SELECT_USER: String = "id,onPremisesSamAccountName,displayName,givenName,surname,streetAddress"
    protected
    val PARAM_VALUE_SELECT_GROUPS: String = "id"


    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    fun azureUriUser( navIdent: String, b: UriBuilder) =
        b.path(USERS_PATH)

            .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_USER)
        .queryParam(PARAM_NAME_FILTER, "onPremisesSamAccountName eq '$navIdent'")
        .queryParam(PARAM_NAME_COUNT, "true")
        .build().also {
                log.trace("URI er: {}", it)
            }


    /**
    fun azureUriGroups(azureID: UUID, b: UriBuilder) =
        b.path(MEMBER_OF_PATH)
        .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_GROUPS)
        .queryParam(PARAM_NAME_COUNT, "true")
        .build()
    **/

    companion object {
        const val GRAPH = "graph"
        private const val DEFAULT_PING_PATH = ""
    }
}