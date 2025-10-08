package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
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
        post<List<Any>>(cf.baseUri, Identer(ids)).also {
            log.info("Oppfølging returnerte ${it.size} identer")
        }.also {
            it.forEach { e ->
                log.info("Oppfølging element: e ${e.javaClass}")
            }
        }
//.map {
          //  OppfølgingsEnhet(Identifikator(it.ident), Enhetsnummer(it.kontorId))
        //}
}
    data class OppfølgingsEnhet(val ident: String, val enhet: Enhetsnummer? = null)
    private data class Identer(val identer: List<String>)
    data class EnhetRespons(val ident: String, val httpStatus: Int?, val kontorId: String)