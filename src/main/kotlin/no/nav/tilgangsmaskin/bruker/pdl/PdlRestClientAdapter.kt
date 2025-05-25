package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@Cacheable(PDL)
class PdlRestClientAdapter(
        @Qualifier(PDL) restClient: RestClient,
        private val cf: PdlConfig,
        private val mapper: ObjectMapper) : AbstractRestClientAdapter(restClient, cf) {

    fun person(brukerId: String) = get<PdlRespons>(cf.personURI, mapOf("ident" to brukerId))

    fun personer(brukerIds: Set<String>) = post<String>(cf.personerURI, brukerIds).let { res ->
       log.info("Henter personer for identer: $brukerIds fra ${cf.personerURI}")
        mapper.readValue<Map<String, PdlRespons?>>(res)
            .mapNotNull { it.value?.let { res -> it.key to res } }
            .toMap().also {
                log.info("Personer hentet $it for identer: $brukerIds fra ${cf.personerURI}")
            }
    }
}


