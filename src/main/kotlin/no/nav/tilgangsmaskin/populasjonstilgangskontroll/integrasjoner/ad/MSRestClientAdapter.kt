package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.client.RestClient
import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class  MSRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, private val cf: MSGraphConfig, errorHandler: ErrorHandler): AbstractRestClientAdapter(
    restClient,cf, errorHandler) {

    fun uuidForIdent(ident: String) = get<Any>(cf.userURI(ident))

    fun grupperForUUID(ansattId: UUID) =
        generateSequence(get<EntraGrupperBolk>(cf.grupperURI(ansattId))) {
            it.next?.let {
                get<EntraGrupperBolk>(it)
            }
        }.flatMap { it.value }.toList()
   }