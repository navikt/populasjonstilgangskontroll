package no.nav.tilgangsmaskin.bruker.pdl

import org.springframework.web.service.annotation.HttpExchange

@HttpExchange
interface PdlGraphQLPingClient {

    @HttpExchange(method = "OPTIONS")
    fun ping()
}

