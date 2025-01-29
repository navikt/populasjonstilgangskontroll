package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf : SkjermingConfig, errorHandler: ErrorHandler): AbstractRestClientAdapter(restClient, cf,errorHandler = errorHandler) {
    fun skjermetPerson(ident: String) =
        restClient
            .post()
            .uri(cf::skjermetUri)
            .accept(APPLICATION_JSON)
            .body(mapOf(IDENT to ident))
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("Skjermet oppslag mot {} OK", cf::skjermetUri)
            }
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<Any>() ?: throw RuntimeException("Ingen respons fra skjermet oppslag mot ${cf::skjermetUri}")
    }


