package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class VergemålRestClientAdapter(@Qualifier(VERGEMÅL) restClient: RestClient, val cf: VergemålConfig) :
    AbstractRestClientAdapter(restClient, cf) {

    fun vergemål(ident: String) =
        post<List<Vergemål>>(cf.vergemålURI, VergemålIdent(ident)).map { it.vergehaver }

    private data class VergemålIdent(val ident: String)

}