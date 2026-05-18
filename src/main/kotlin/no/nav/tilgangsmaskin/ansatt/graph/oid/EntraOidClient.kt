package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.PARAM_NAME_COUNT
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.PARAM_NAME_FILTER
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.PARAM_NAME_SELECT
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.PARAM_VALUE_SELECT_USER
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface EntraOidClient {

    @GetExchange(ENTRA_PING_PATH)
    fun ping(): Any

    @GetExchange(ENTRA_USERS_PATH)
    fun oid(@RequestParam(PARAM_NAME_FILTER) filter: String,
            @RequestParam(PARAM_NAME_SELECT) select: String = PARAM_VALUE_SELECT_USER,
            @RequestParam(PARAM_NAME_COUNT) count: Boolean = true): EntraOidRespons

    companion object {
        const val ENTRA_PING_PATH = "/organization"
        const val ENTRA_USERS_PATH = "/users"
        fun filter(ansattId: AnsattId) = "onPremisesSamAccountName eq '${ansattId.verdi}'"
    }
}