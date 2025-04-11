package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@Cacheable(PDL)
class PdlRestClientAdapter(@Qualifier(PDL) restClient: RestClient, private val cf : PdlConfig, private val mapper: ObjectMapper): AbstractRestClientAdapter(restClient, cf) {

    fun person(brukerId: String) = get<PdlRespons>(cf.personURI(), mapOf("ident" to brukerId))

    fun personer(brukerIds: List<String>) = post<String>(cf.personerURI(), brukerIds).let { res ->
        mapper.readValue<Map<String, PdlRespons?>>(res)
            .mapNotNull {  it.value?.let {res -> it.key to res } }
            .toMap()
    }
}


