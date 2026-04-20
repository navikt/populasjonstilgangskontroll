package no.nav.tilgangsmaskin.ansatt.entraproxy
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ANSATT_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENHETER_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.PING_PATH
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface EntraProxyClient {

    @GetExchange(ANSATT_PATH)
    fun ansatt(@PathVariable navIdent: String): EntraProxyAnsatt

    @GetExchange(ENHETER_PATH)
    fun enheter(@PathVariable navIdent: String, @RequestHeader(IDENTIFIKATOR) identifikator: String): Set<Enhet>

    @GetExchange(PING_PATH)
    fun ping(): Any

    companion object {
        const val ANSATT_PATH = "/api/v1/ansatt/{navIdent}"
        const val ENHETER_PATH = "/api/v1/enhet/ansatt/{navIdent}"
        const val PING_PATH = "/monitoring/health/liveness"
    }
}
