package no.nav.tilgangsmaskin.ansatt.entra

import java.net.URI
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(GRAPH)
class EntraConfig(
    baseUri: URI,
    override val initialCacheSize : Int = 100,
    override val maxCacheSize : Int  = 25000,
    override val expireHours : Long = 12,
    pingPath: String = DEFAULT_PING_PATH,
    private val size: Int = DEFAULT_BATCH_SIZE,
    enabled: Boolean = true) : CachableRestConfig, AbstractRestConfig(baseUri, pingPath, GRAPH, enabled) {

    fun userURI(navIdent: String) = builder().path(USERS_PATH)
        .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_USER)
        .queryParam(PARAM_NAME_FILTER, "onPremisesSamAccountName eq '$navIdent'")
        .queryParam(PARAM_NAME_COUNT, "true")
        .build()

    fun grupperURI(ansattId: String) = builder().path(GRUPPER_PATH)
        .queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_GROUPS)
        .queryParam(PARAM_NAME_COUNT, "true")
        .queryParam(PARAM_NAME_TOP, size)
        .queryParam(PARAM_NAME_FILTER, "startswith(displayName,'0000-GA-GEO')")
        .build(ansattId)

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"


    companion object {
        const val GRAPH = "graph"
        val HEADER_CONSISTENCY_LEVEL = "ConsistencyLevel" to "eventual"
        private const val DEFAULT_BATCH_SIZE = 250
        private const val USERS_PATH = "/users"
        private const val GRUPPER_PATH = "/users/{ansattId}/memberOf"
        private const val PARAM_NAME_SELECT = "\$select"
        private const val PARAM_NAME_FILTER = "\$filter"
        private const val PARAM_NAME_COUNT = "\$count"
        private const val PARAM_VALUE_SELECT_USER = "id"
        private const val PARAM_VALUE_SELECT_GROUPS = "id,displayName"
        private const val DEFAULT_PING_PATH = "/organization"
        private const val PARAM_NAME_TOP = "\$top"
    }
}