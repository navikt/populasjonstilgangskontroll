package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipConfig.Companion.PDLPIP
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@Cacheable(PDLPIP)
class PdlPipRestClientAdapter(@Qualifier(PDLPIP) restClient: RestClient, private val cf : PdlPipConfig): AbstractRestClientAdapter(restClient, cf) {

    fun person(brukerId: String) = get<PdlPipRespons>(cf.personURI(), mapOf("ident" to brukerId))

    fun personer(brukerIds: List<String>) = post<Any>(cf.personerURI(), brukerIds).let {
        log.info("PdlPip respons: $it")
        it as Map<String, PdlPipRespons>
    }
}


