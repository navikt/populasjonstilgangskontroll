package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class VergemålRestClientAdapter(@Qualifier(VERGEMÅL) restClient: RestClient,/* @Qualifier(VERGEMÅL + "ping") pingClient: RestClient*/ val cf: VergemålConfig) :
    AbstractRestClientAdapter(restClient, cf, /*pingClient*/) {



    fun vergemål(ident: String): List<BrukerId> {
        log.info("Henter vergemål for $ident")
        val respons =  post<List<Vergemål>>(cf.vergemålURI, VergemålIdent(ident))
        log.info("Hentet vergemål $respons for $ident")
        return respons.map { it.vergehaver }
    }

    private data class VergemålIdent(val ident: String)

}