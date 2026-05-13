package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.PARAM_NAME_FILTER
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.PARAM_NAME_SELECT
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.PARAM_NAME_COUNT
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.PARAM_VALUE_SELECT_USER
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import java.util.*

@HttpExchange
interface EntraClient {

    @GetExchange(ENTRA_PING_PATH)
    fun ping(): Any

    @GetExchange(ENTRA_USERS_PATH)
    fun findUser(
        @RequestParam(PARAM_NAME_FILTER) filter: String,
        @RequestParam(PARAM_NAME_SELECT) select: String = PARAM_VALUE_SELECT_USER,
        @RequestParam(PARAM_NAME_COUNT) count: Boolean = true): UserResponse

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class UserResponse(@param:JsonProperty("value") val oids: Set<OidEntry>) {
        data class OidEntry(val id: UUID)
    }

    companion object {
        const val ENTRA_PING_PATH = "/organization"
        const val ENTRA_USERS_PATH = "/users"
    }
}
