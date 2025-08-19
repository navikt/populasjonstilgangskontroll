package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val valkey: ValKeyAdapter) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(ident: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to ident))

    fun skjerminger(identer: Set<String>): Map<BrukerId, Boolean> {
        if (identer.isEmpty()) return emptyMap()
        else {
            val cached = skjermingerFraCache(identer)
            val slåttOpp = skjermingerFraREST(identer/*.minus(cached.keys)*/)
            log.info("Hentet ${cached.size} skjerminger fra cache, ${slåttOpp.size} fra REST av totalt ${identer.size} identer")
            //todo lagre slåttOpp i cache
            return slåttOpp
            //  return (cached  + slåttOpp).map { BrukerId(it.key) to it.value }.toMap().also {
            //      log.info("Totalt $it skjerminger")
            // }
        }
    }

    private fun skjermingerFraREST(identer: Set<String>): Map<BrukerId, Boolean> =
        post<Map<String, Boolean>>(cf.skjermingerUri, mapOf(IDENTER to identer))
            .map { (brukerId, skjerming) -> BrukerId(brukerId) to skjerming }
            .toMap()

    private fun skjermingerFraCache(identer: Set<String>) =
        runCatching {
            valkey.skjerminger(*identer.toTypedArray())
                .associate { (ident, skjerming) -> ident to skjerming }
        }.getOrElse {
            log.warn("Kunne ikke hente skjerminger for ${identer} identer fra cache",it)
            emptyMap()
        }
}


