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
        runBlocking {
            val attributter = async { adapter.attributter(ident.verdi) }
            val grupper = async { adapter.grupper(ident.verdi) }
             EntraResponse(attributter.await(), grupper.await())
        }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}

data class EntraResponse(val attributter: Ansatt.AnsattAttributter, val grupper: List<EntraGruppe>)

