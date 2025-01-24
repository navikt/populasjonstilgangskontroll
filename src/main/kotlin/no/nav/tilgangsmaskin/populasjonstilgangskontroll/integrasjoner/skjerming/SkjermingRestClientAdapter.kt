package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.Fødselsnummer

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class SkjermingRestClientAdapter(@Qualifier(SkjermingConfig.Companion.SKJERMING) restClient: RestClient, private val cf : SkjermingConfig): AbstractRestClientAdapter(restClient, cf) {
    fun skjermetPerson(ident: Fødselsnummer)=
        restClient
            .get()
            .uri(cf::skjermetUri)
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("skjermet ${cf::skjermetUri} OK")
            }
            .body<Any>()
    }


