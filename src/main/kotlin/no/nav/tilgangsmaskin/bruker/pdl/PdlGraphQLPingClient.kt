package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.service.annotation.HttpExchange

@ClientRegistrationId(PDLGRAPH)
interface PdlGraphQLPingClient {

    @HttpExchange(method = "OPTIONS")
    fun ping()
}

