package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDLPIP
import no.nav.tilgangsmaskin.felles.rest.RestDefaultErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

@ClientRegistrationId(PDLPIP)
interface PdlPipClient {

    @GetExchange(PDL_PIP_PERSON_PATH)
    fun person(@RequestHeader("ident") ident: String,
               @RequestHeader(IDENTIFIKATOR) identifikator: String): PdlPipRespons

    @PostExchange(PDL_PIP_PERSONER_PATH)
    fun personer(@RequestBody identer: Set<String>): Map<String, PdlPipRespons?>

    @GetExchange(PDL_PIP_PING_PATH)
    fun ping(): Any

    companion object {
        const val PDL_PIP_PERSON_PATH = "/api/v1/person"
        const val PDL_PIP_PERSONER_PATH = "/api/v1/personBolk"
        const val PDL_PIP_PING_PATH = "/internal/health/liveness"
    }
}

