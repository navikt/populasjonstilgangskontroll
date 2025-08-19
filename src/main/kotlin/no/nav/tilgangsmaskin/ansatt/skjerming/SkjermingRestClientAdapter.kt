package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val valkey: ValKeyAdapter) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(ident: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to ident))

    fun skjerminger(identer: Set<String>) =
        if (identer.isEmpty()) emptyMap()
        else {runCatching {
            skjermingerFraCache(identer).also {
                log.info("Hentet $it skjerminger for ${identer} identer fra cache")
            }
        }.getOrElse {
            log.warn("Kunne ikke hente skjerminger for ${identer} identer fra cache",it)
            emptyMap()
        }
            post<Map<String, Boolean>>(cf.skjermingerUri, mapOf(IDENTER to identer))
                .map { (brukerId, skjerming) -> BrukerId(brukerId) to skjerming }
                .toMap()
        }

    private fun skjermingerFraCache(identer: Set<String>) =
        valkey.skjerminger(*identer.toTypedArray())
            .associate { (brukerId, skjerming) -> BrukerId(brukerId) to skjerming }
}


