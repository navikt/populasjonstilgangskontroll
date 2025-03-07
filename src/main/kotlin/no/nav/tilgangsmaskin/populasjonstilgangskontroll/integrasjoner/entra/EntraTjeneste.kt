package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService

@RetryingOnRecoverableCacheableService(cacheNames = [GRAPH])
class EntraTjeneste(private val adapter: EntraClientAdapter) {

    fun ansatt(ident: AnsattId)=
        run {
            val attributter =  adapter.attributter(ident.verdi).attributter.first()
            val grupper = adapter.grupper("${attributter.id}")
             EntraResponse(attributter.tilAttributter(), grupper)
        }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}

private fun EntraSaksbehandlerResponse.MSGraphSaksbehandlerAttributter.tilAttributter() = EntraResponsMapper.mapAttributter(this)

data class EntraResponse(val attributter: Ansatt.AnsattAttributter, val grupper: List<EntraGruppe>)

