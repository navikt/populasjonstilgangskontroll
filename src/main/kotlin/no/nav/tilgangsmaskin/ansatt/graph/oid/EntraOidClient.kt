package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface EntraOidClient {

    @GetExchange(ENTRA_PING_PATH)
    fun ping(): Any

    @GetExchange(ENTRA_USERS_PATH)
    fun oid(
        @RequestParam(EntraGrupperConfig.PARAM_NAME_FILTER) filter: String,
        @RequestParam(EntraGrupperConfig.PARAM_NAME_SELECT) select: String = EntraGrupperConfig.PARAM_VALUE_SELECT_USER,
        @RequestParam(EntraGrupperConfig.PARAM_NAME_COUNT) count: Boolean = true): EntraOidRespons

    companion object {
        const val ENTRA_PING_PATH = "/organization"
        const val ENTRA_USERS_PATH = "/users"
        fun accountFilter(ansattId: AnsattId) = "onPremisesSamAccountName eq '${ansattId.verdi}'"
    }
}