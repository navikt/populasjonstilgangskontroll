package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.CacheName
import no.nav.tilgangsmaskin.felles.rest.cache.ValkeyCacheClient
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient



@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val cache: ValkeyCacheClient, private val teller : BulkCacheSuksessTeller) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(ident: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to ident))

    fun skjerminger(ids: Set<String>): Map<BrukerId, Boolean> {
        val fraCache = fraCache(ids)
        if (fraCache.size == ids.size) {
            teller.tell(Tags.of("name", SKJERMING_CACHE.name,"suksess","true"))
        }
        val fraRest = fraRest(ids - fraCache.keys)
        cache.put(SKJERMING_CACHE, fraRest)
        teller.tell(Tags.of("name", SKJERMING_CACHE.name,"suksess","false"))
        return (fraRest + fraCache).mapKeys {  BrukerId(it.key) }
    }
    private fun fraRest(identer: Set<String>) =
        if (identer.isEmpty()) {
            emptyMap()
        }
        else {
            post<Map<String, Boolean>>(cf.skjermingerUri, mapOf(IDENTER to identer))
        }

    private fun fraCache(ids: Set<String>) =
            cache.mget<Boolean>(SKJERMING_CACHE, ids)

    companion object {
        const val EXTRA = "medNærmesteFamilie"
        private val SKJERMING_CACHE = CacheName(SKJERMING)
    }
}


