package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient


@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val cache: CacheOperations, private val teller : BulkCacheSuksessTeller) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(id: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to id))

    fun skjerminger(ids: Set<String>): Map<BrukerId, Boolean> {
        val fraCache = fraCache(ids)
        if (fraCache.size == ids.size) {
            tell(true)
            return fraCache.mapKeys { BrukerId(it.key) }
        }
        val fraRest = fraRest(ids - fraCache.keys)
        cache.putMany(fraRest,cf.varighet,SKJERMING_CACHE)
        tell(false)
        return (fraRest + fraCache).mapKeys {  BrukerId(it.key) }
    }
    private fun fraRest(ids: Set<String>) =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else {
            post<Map<String, Boolean>>(cf.skjermingerUri, mapOf(IDENTER to ids))
        }

    private fun tell(status: Boolean) =
        teller.tell(Tags.of("name", SKJERMING_CACHE.name,"suksess",status.toString()))
    private fun fraCache(ids: Set<String>) =
            cache.getMany(ids, Boolean::class,SKJERMING_CACHE)

}


