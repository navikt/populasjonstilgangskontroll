package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyCacheAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val cache: ValKeyCacheAdapter) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(ident: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to ident))

    fun skjerminger(identer: Set<String>): Map<BrukerId, Boolean> {
        val fraCache = fraCache(identer)
        val fraRest = fraRest(identer - fraCache.keys)
        cache.put(SKJERMING, fraRest)
        return (fraRest + fraCache).mapKeys {  BrukerId(it.key) }
    }
    private fun fraRest(identer: Set<String>) =
        if (identer.isEmpty()) {
            emptyMap()
        }
        else {
            post<Map<String, Boolean>>(cf.skjermingerUri, mapOf(IDENTER to identer)).also {
                log.info("Hentet ${it.size} skjerming(er) fra REST for ${identer.size} ident(er)")
            }
        }


    private fun fraCache(identer: Set<String>) =
        if (identer.isEmpty()) {
            emptyMap()
        }
        else  {
            cache.skjerminger(identer)
                .associate { (ident, erSkjermet) -> ident to erSkjermet }.also {
                    log.info("Hentet ${it.size} skjerming(er) fra cache for ${identer.size} ident(er)")
                }
        }
}


