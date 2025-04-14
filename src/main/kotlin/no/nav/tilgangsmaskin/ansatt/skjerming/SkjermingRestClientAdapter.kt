package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig) :
    AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(ident: String) = post<Boolean>(cf.skjermetUri(), mapOf(IDENT to ident))
    fun skjerminger(identer: List<String>) = post<Map<String, Boolean>>(cf.skjermetBulkUri(), mapOf(IDENTER to identer))
        .map { BrukerId(it.key) to it.value }
        .toMap()
}


