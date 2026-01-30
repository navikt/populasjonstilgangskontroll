package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient


@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val cache: CacheClient, private val teller : BulkCacheSuksessTeller) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(id: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to id))

    fun skjerminger(ids: Set<String>): Map<BrukerId, Boolean> {
        val resultat = hentMedCache<Boolean>(
            ids = ids,
            cache = cache,
            cacheConfig = SKJERMING_CACHE,
            ttl = cf.varighet,
            restUri = cf.skjermingerUri,
            restBody = mapOf(IDENTER to ids)
        )
        tell(resultat.size == ids.size)
        return resultat.mapKeys { BrukerId(it.key) }
    }
    private fun tell(status: Boolean) =
        teller.tell(Tags.of("name", SKJERMING_CACHE.name,"suksess",status.toString()))

}


