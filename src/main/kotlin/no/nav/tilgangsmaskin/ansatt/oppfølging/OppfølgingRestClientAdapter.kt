package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OppfølgingRestClientAdapter(@Qualifier(OPPFØLGING) restClient: RestClient, val cf: OppfølgingConfig) :
    AbstractRestClientAdapter(restClient, cf) {
    fun enheterFor(ids: List<String>) =
        post<List<Map<Any,Any>>>(cf.baseUri, Identer(ids)).map {
            OppfølgingsEnhet(Identifikator(it["ident"] as String), (it["kontorId"] as String?)?.let { Enhetsnummer(it) })
        }
}
data class OppfølgingsEnhet(val ident: Identifikator, val enhet: Enhetsnummer? = null)
private data class Identer(val identer: List<String>)
