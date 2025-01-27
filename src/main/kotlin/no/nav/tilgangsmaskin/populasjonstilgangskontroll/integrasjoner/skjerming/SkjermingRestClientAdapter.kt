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

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf : SkjermingConfig): AbstractRestClientAdapter(restClient, cf) {
    fun skjermetPerson(ident: String) =
        restClient
            .post()
            .uri(cf::skjermetUri)
            .accept(APPLICATION_JSON)
            .body { mapOf(IDENT to ident) }
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("skjermet oppslag mot {} OK", cf::skjermetUri)
            }
            .onStatus(HttpStatusCode::isError) { _, _ ->
                throw RuntimeException("skjermet oppslag mot ${cf::skjermetUri} feilet")
            }
            .body<Boolean>() ?: throw RuntimeException("Ingen respons fra skjermet oppslag mot ${cf::skjermetUri}")
    }


