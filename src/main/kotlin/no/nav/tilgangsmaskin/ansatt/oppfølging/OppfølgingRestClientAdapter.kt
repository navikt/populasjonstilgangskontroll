package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OppfølgingRestClientAdapter(@Qualifier(OPPFØLGING) restClient: RestClient, val cf: OppfølgingConfig) :
    AbstractRestClientAdapter(restClient, cf) {
    fun enhetFor(id: List<String>) =
         post<Any>(cf.baseUri, id)
}
