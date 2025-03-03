package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipConfig.Companion.PDLPIP
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
@Cacheable(PDLPIP)
class PdlPipRestClientAdapter(@Qualifier(PDLPIP) restClient: RestClient, private val cf : PdlPipConfig, errorHandler: ErrorHandler): AbstractRestClientAdapter(restClient, cf, errorHandler) {

    fun person(brukerId: String) = get<PdlPipRespons>(cf.personURI(), mapOf("ident" to brukerId)).also { log.info("PDLPIP respons : $it") }
    fun bolk(brukerIds: List<String>) = post<Any>(cf.personBolkURI(), brukerIds)
}


