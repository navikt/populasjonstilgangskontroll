package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.uuids
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker.Companion.GEO
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker.Companion.GEO_OG_GLOBALE
import no.nav.tilgangsmaskin.ansatt.graph.EntraClient.Companion.ENTRA_PING_PATH
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.stereotype.Component
import java.net.URI
import java.time.Duration

@Component
class EntraConfig : CachableRestConfig, RestConfig(BASE_URI, ENTRA_PING_PATH, GRAPH) {

    override val caches = ENTRA_CACHES
    override val navn = name
    override val varighet = Duration.ofHours(3)

    fun grupperURI(ansattId: String, isCCF: Boolean) =
         if (isCCF) ccUri(ansattId) else oboUri(ansattId)

    private fun oboUri(ansattId: String) =
        query(ansattId,GEO_PREFIX)

    private fun ccUri(ansattId: String) =
        query(ansattId,"id in(${uuidsFormatted()}) or $GEO_PREFIX")

    private fun query(ansattId: String, filter: String) =
        builder().apply {
            path(GRUPPER_PATH)
            queryParam(PARAM_NAME_SELECT, PARAM_VALUE_SELECT_GROUPS)
            queryParam(PARAM_NAME_COUNT, "true")
            queryParam(PARAM_NAME_TOP, "$DEFAULT_BATCH_SIZE")
            queryParam(PARAM_NAME_FILTER, filter)
        }.build(ansattId)

    private fun uuidsFormatted() =
        uuids().joinToString("','" , "'",  "'")

    companion object {
        private const val DEFAULT_BATCH_SIZE = 250
        private const val GRUPPER_PATH = "/users/{ansattId}/memberOf"
        private const val PARAM_VALUE_SELECT_GROUPS = "id,displayName"
        private const val PARAM_NAME_TOP = "\$top"
        private const val GRAPH_URL = "https://graph.microsoft.com/v1.0/"
        const val GRAPH = "graph"

        const val PARAM_NAME_SELECT = "\$select"
        const val PARAM_NAME_FILTER = "\$filter"
        const val PARAM_NAME_COUNT = "\$count"
        const val PARAM_VALUE_SELECT_USER = "id"
        const val GEO_PREFIX = "startswith(displayName,'0000-GA-GEO') or startswith(displayName,'0000-GA-ENHET') "
        val BASE_URI = URI.create(GRAPH_URL)
        val OID_CACHE = CachableConfig(ENTRA_OID)
        val ENTRA_CACHES = setOf(CachableConfig(GRAPH,GEO), CachableConfig(GRAPH,GEO_OG_GLOBALE))

    }
}