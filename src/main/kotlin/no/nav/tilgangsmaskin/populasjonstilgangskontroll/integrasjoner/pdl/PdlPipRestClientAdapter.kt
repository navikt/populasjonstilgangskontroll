package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipConfig.Companion.PDLPIP
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@Cacheable(PDLPIP)
class PdlPipRestClientAdapter(@Qualifier(PDLPIP) restClient: RestClient, private val cf : PdlPipConfig, private val mapper: ObjectMapper): AbstractRestClientAdapter(restClient, cf) {

    fun person(brukerId: String) = get<PdlPipRespons>(cf.personURI(), mapOf("ident" to brukerId))

    fun personer(brukerIds: List<String>) = post<String>(cf.personerURI(), brukerIds).let { res ->
        mapper.readValue<Map<String, PdlPipRespons?>>(res)
            .mapNotNull { (key, value) -> value?.let { BrukerId(key) to it } }
            .toMap()
    }
}


