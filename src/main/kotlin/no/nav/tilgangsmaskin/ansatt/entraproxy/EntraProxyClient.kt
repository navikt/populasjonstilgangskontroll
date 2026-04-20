package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface EntraProxyClient {

    @GetExchange("/api/v1/ansatt/{navIdent}")
    fun ansatt(@PathVariable navIdent: String): EntraProxyAnsatt

    @GetExchange("/api/v1/enhet/ansatt/{navIdent}")
    fun enheter(@PathVariable navIdent: String): Set<Enhet>

    @GetExchange("/monitoring/health/liveness")
    fun ping(): Any
}
