package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService

@RetryingOnRecoverableCacheableService(cache = GRAPH)
class EntraTjeneste(private val adapter: EntraClientAdapter) {

    fun ansatt(ident: AnsattId) : Ansatt {
        val attributter = adapter.attributter(ident.verdi)
        val grupper = adapter.grupper("${attributter.id}")
        return Ansatt(attributter,*grupper)
    }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}

